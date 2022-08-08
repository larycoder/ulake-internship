from flask import Flask, jsonify
from PIL import Image, ImageFont, ImageDraw, ImageEnhance, ImageChops
from io import BytesIO
import pandas
import numpy as np
import cv2, os
from keras_retinanet.utils.image import read_image_bgr, preprocess_image
from keras_retinanet.utils.colors import label_color

from keras_retinanet.utils.visualization import draw_box, draw_caption, draw_annotations
import math
import shutil
from tensorflow.keras.models import load_model
from skimage import exposure
from skimage import io
from skimage import transform
import time
import base64

from keras_maskrcnn import models

import warnings
warnings.filterwarnings("ignore")

application = Flask(__name__)

#model_path = os.path.join('/storage/cuongnc/LUNA/3d_nodule_detection/nodule_boudingbox/keras-maskrcnn/snapshots//mix/', 'resnet50_csv_53.h5')
model_path = os.path.join('/storage/cuongnc/LUNA/3d_nodule_detection/nodule_boudingbox/keras-maskrcnn_new_222222', 'snapshots/resnet50_csv_07.h5')
if os.path.exists(model_path):
	print(model_path)
else:
	print("No model exist")
#load retinanet model
print(model_path)
model = models.load_model(model_path, backbone_name='resnet50')

def draw_mask(image, box, mask, label=None, color=None, binarize_threshold=0.5):
	""" Draws a mask in a given box.

	Args
		image              : Three dimensional image to draw on.
		box                : Vector of at least 4 values (x1, y1, x2, y2) representing a box in the image.
		mask               : A 2D float mask which will be reshaped to the size of the box, binarized and drawn over the image.
		color              : Color to draw the mask with. If the box has 5 values, the last value is assumed to be the label and used to construct a default color.
		binarize_threshold : Threshold used for binarizing the mask.
	"""
	if label is not None:
		color = label_color(label)
	if color is None:
		color = (255, 0, 0)

	# resize to fit the box
	mask = mask.astype(np.float32)
	mask = cv2.resize(mask, (box[2] - box[0], box[3] - box[1]))

	# binarize the mask
	mask = (mask > binarize_threshold).astype(np.uint8)

	# draw the mask in the image
	mask_image = np.zeros((image.shape[0], image.shape[1]), np.uint8)
	mask_image[box[1]:box[3], box[0]:box[2]] = mask
	mask = mask_image

	# compute a nice border around the mask
	border = mask - cv2.erode(mask, np.ones((5, 5), np.uint8), iterations=1)

	# apply color to the mask and border
	#mask = (np.stack([mask] * 3, axis=2) * color).astype(np.uint8)
	border = (np.stack([border] * 3, axis=2) * (255, 255, 255)).astype(np.uint8)

	# # draw the mask
	# indices = np.where(mask != [0, 0, 0])
	# image[indices[0], indices[1], :] = 0.5 * image[indices[0], indices[1], :] + 0.5 * mask[indices[0], indices[1], :]

	# draw the border
	indices = np.where(border != [0, 0, 0])
	image[indices[0], indices[1], :] = 0.2 * image[indices[0], indices[1], :] + 0.8 * border[indices[0], indices[1], :]

def calculateNoduleSize(x1, y1, x2, y2):
	dist = math.sqrt((float(x2) - float(x1)) ** 2 + (float(y2) - float(y1)) ** 2)
	return dist

def nms(output, nms_th):
	if len(output) == 0:
		return output

	#output = np.array(output)
	output = np.array(output, dtype=object)
	output = output[np.argsort(-output[:, 0])]
	#print("1 ", output)

	bboxes = [output[0]]
	print("Lenghth ", len(output))
	
	for i in np.arange(1, len(output)):
		bbox = output[i]
		bbox2 = np.array([output[i][1], output[i][2][0], output[i][2][1], output[i][3]])
		flag = 1
		for j in range(len(bboxes)):
			bboxes2 = np.array([bboxes[j][1], bboxes[j][2][0], bboxes[j][2][1], bboxes[j][3]])
			#print(bbox2, bboxes2)
			if iou(bbox2, bboxes2) >= nms_th:
				flag = -1
				break
		if flag == 1:
			#print("OKKKKKKKKKK")
			bboxes.append(bbox)
	
	bboxes = np.asarray(bboxes, dtype=object)
	return bboxes

def iou(box0, box1):
	
	r0 = box0[3] / 2
	s0 = box0[:3] - r0
	e0 = box0[:3] + r0

	r1 = box1[3] / 2
	s1 = box1[:3] - r1
	e1 = box1[:3] + r1

	overlap = []
	for i in range(len(s0)):
		overlap.append(max(0, min(e0[i], e1[i]) - max(s0[i], s1[i])))

	intersection = overlap[0] * overlap[1] * overlap[2]
	union = box0[3] * box0[3] * box0[3] + box1[3] * box1[3] * box1[3] - intersection
	return intersection / union

def trim(im):
	bg = Image.new(im.mode, im.size, im.getpixel((0,0)))
	diff = ImageChops.difference(im, bg)
	diff = ImageChops.add(diff, diff, 1.0, -50)
	bbox = diff.getbbox()
	if bbox:
		return im.crop(bbox)
def compute_resize_scale(image_shape, min_side=512, max_side=512):
	""" Compute an image scale such that the image size is constrained to min_side and max_side.
	Args
		min_side: The image's min side will be equal to min_side after resizing.
		max_side: If after resizing the image's max side is above max_side, resize until the max side is equal to max_side.
	Returns
		A resizing scale.
	"""
	(rows, cols, _) = image_shape

	smallest_side = min(rows, cols)

	# rescale the image so the smallest side is min_side
	scale = min_side / smallest_side

	# check if the largest side is now greater than max_side, which can happen
	# when images have a large aspect ratio
	largest_side = max(rows, cols)
	if largest_side * scale > max_side:
		scale = max_side / largest_side

	return scale
	
def resize_image(img, min_side=512, max_side=512):
	# compute scale to resize the image
	scale = compute_resize_scale(img.shape, min_side=min_side, max_side=max_side)

	# resize the image with the computed scale
	img = cv2.resize(img, None, fx=scale, fy=scale)

	return img, scale

@application.route('/classify/<string:patient_id>/')
def classify(patient_id):
	#df_node = pandas.read_csv("K_list_patients.csv")
	df_node = pandas.read_csv("patients3cm.csv")
	df_patient = df_node[df_node["Patient_ID"] == patient_id]
	#patient_name =  df_patient["Patient_Name"].to_string(index=False).encode("utf-8")
	patient_name =  df_patient["Patient_Name"].to_string(index=False)
	
	label = np.array([['0','benign'],  ['1','malignant']])
	labelNames = label[:,1]

	classify_model = load_model('/storage/cuongnc/LUNA/nodule_classification/train_log/best_model.hdf5')

	print(df_patient)
	print("Classifying with patient: ", patient_name)
	# if patient_name.startswith("u' "):
	# 	patient_name = patient_name.replace("u' ", "")
	if patient_name.startswith(" "):
		patient_name = patient_name.strip()
		patient_name = patient_name.replace(" ", "_")

	print("patient_name", patient_name)
	k1_prep_result_path = '/storage/cuongnc/LUNA/3d_nodule_detection/nodule_boudingbox/k_data/K_output_1/'
	k2_prep_result_path = '/storage/cuongnc/LUNA/3d_nodule_detection/nodule_boudingbox/k_data/K_output_2/'
	if(os.path.isfile(k1_prep_result_path+patient_name+'_clean.npy')):
		image_path = k1_prep_result_path+patient_name+'_clean.npy'
	elif(os.path.isfile(k2_prep_result_path+patient_name+'_clean.npy')):
		image_path = k2_prep_result_path+patient_name+'_clean.npy'

	else:
		print("Not found image file", patient_name)

	img_array = np.load(image_path)
	print("image shape: ",img_array.shape)
	
	labels_to_names = {0: 'nodule'}
	classify_label = {0: 'benign', 1: 'malignant' }
	threshold = 0.5
	predict_list = []
	shutil.rmtree('test/')
	if not os.path.exists("test/"):
		os.mkdir("test/")

	for i in range(img_array.shape[0]):
		# preprocess image for network
		cv2.imwrite("test/" + str(i) +".jpg", img_array[i])
		image = read_image_bgr("test/" + str(i) +".jpg")
		pre_image = preprocess_image(image)
		pre_image, scale = resize_image(pre_image)

		image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

		outputs = model.predict_on_batch(np.expand_dims(pre_image, axis=0))
		boxes  = outputs[-4][0]
		scores = outputs[-3][0]
		labels = outputs[-2][0]
		masks  = outputs[-1][0]

		# correct for image scale
		boxes /= scale
		for box, score, label, mask in zip(boxes, scores, labels, masks):
		# scores are sorted so we can break
			if score < threshold:
				break
			else:
				size = calculateNoduleSize(box[0], box[1], box[2], box[3])
				predict_list.append([score, int(i), box, size, label, mask, image])
				#predict_list.append([score, int(i), box[0], box[1], size])

				#print(str(i) +".jpg", i,  labels_to_names[label], score)

	probabilities = nms(predict_list,0.05)
	#print(probabilities)
	#print("Lenghth ", len(probabilities))

	pre_results = []

	for i in range(len(probabilities)):

		slice_index = img_array.shape[0] - probabilities[i][1]
		color = label_color(probabilities[i][4])
		b = probabilities[i][2].astype(int)
		mask = probabilities[i][5]
		label = probabilities[i][4]
		score = probabilities[i][0]
		image = probabilities[i][6]
		nodule_pbb = float(score) * 100

		nodule_file_path = "test/" + str(i) +"_crop.jpg"
		box = b.copy()
		box = np.array(box).astype(int)
		crop_image = image[box[1]:box[3], box[0]:box[2]]
		crop_image = cv2.resize(crop_image, (64,64), interpolation = cv2.INTER_AREA)
		#print(nodule_file_path)
		cv2.imwrite(nodule_file_path, crop_image)
		classify_image = io.imread(nodule_file_path,as_gray=True)
		#classify_image = transform.resize(classify_image, (64, 64))
		classify_image = exposure.equalize_adapthist(classify_image, clip_limit=0.1)
		classify_image = classify_image.astype("float32") / 255.0
		classify_image = np.expand_dims(classify_image, axis=0)
		classify_image = np.expand_dims(classify_image, axis=-1)
		#print(classify_image.shape)
		preds = classify_model.predict(classify_image)
		j = preds.argmax(axis=1)[0]
		pred_label = labelNames[j]
		score = preds[0][j]
		#print(pred_label, score)
		caption = "{} {:.3f}".format(pred_label, round(score, 3))
		draw_caption(image, b, caption)
		print("Found a candidate at slide number " + str(slice_index)+ ' and have ' +str(round(score, 3)*100)+ "% to be " + pred_label)


		draw_box(image, b, color=(255, 0, 0),thickness=1)
		mask = mask[:, :, label]
		draw_mask(image, b, mask, color=label_color(label))

		buff = BytesIO()
		showImage = Image.fromarray(image).convert("RGBA")
		#showImage = image.resize((700, 450),Image.ANTIALIAS)
		#showImage = trim(image)
		showImage.save(buff, format="PNG")
		img_str = str(base64.b64encode(buff.getvalue()), 'utf-8')

		pre_results.append([slice_index,nodule_pbb,img_str])


	return jsonify({'pre_results':pre_results})
	#return "Hello World at " + time.time().__str__() + "\n";


@application.route('/detect/<string:patient_id>/')
def detect(patient_id):
	print("Detecting")

	df_node = pandas.read_csv("patients3cm.csv")
	df_patient = df_node[df_node["Patient_ID"] == patient_id]
	#patient_name =  df_patient["Patient_Name"].to_string(index=False).encode("utf-8")
	patient_name =  df_patient["Patient_Name"].to_string(index=False)
	
	if patient_name.startswith(" "):
		patient_name = patient_name.strip()
		patient_name = patient_name.replace(" ", "_")
		print(patient_name)

	k1_prep_result_path = '/storage/cuongnc/LUNA/3d_nodule_detection/nodule_boudingbox/k_data/K_output_1/'
	k2_prep_result_path = '/storage/cuongnc/LUNA/3d_nodule_detection/nodule_boudingbox/k_data/K_output_2/'
	if(os.path.isfile(k1_prep_result_path+patient_name+'_clean.npy')):
		image_path = k1_prep_result_path+patient_name+'_clean.npy'
	elif(os.path.isfile(k2_prep_result_path+patient_name+'_clean.npy')):
		image_path = k2_prep_result_path+patient_name+'_clean.npy'
	else:
		print("Not found image file", patient_name)

	img_array = np.load(image_path)
	print("image shape: ",img_array.shape)
	labels_to_names = {0: 'nodule'}
	threshold = 0.5

	predict_list = []

	for i in range(img_array.shape[0]):
		# preprocess image for network
		cv2.imwrite("test/" + str(i) +".jpg", img_array[i])
		image = read_image_bgr("test/" + str(i) +".jpg")
		pre_image = preprocess_image(image)
		pre_image, scale = resize_image(pre_image)

		image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

		outputs = model.predict_on_batch(np.expand_dims(pre_image, axis=0))
		boxes  = outputs[-4][0]
		scores = outputs[-3][0]
		labels = outputs[-2][0]
		masks  = outputs[-1][0]

		# correct for image scale
		boxes /= scale
		for box, score, label, mask in zip(boxes, scores, labels, masks):
		# scores are sorted so we can break
			if score < threshold:
				break
			else:
				size = calculateNoduleSize(box[0], box[1], box[2], box[3])
				predict_list.append([score, int(i), box, size, label, mask, image])
				#predict_list.append([score, int(i), box[0], box[1], size])

				print(str(i) +".jpg", i,  labels_to_names[label], score)

	probabilities = nms(predict_list,0.05)
	#print(probabilities)
	print("Lenghth ", len(probabilities))

	pre_results = []

	for i in range(len(probabilities)):

		slice_index = img_array.shape[0] - probabilities[i][1]
		color = label_color(probabilities[i][4])
		b = probabilities[i][2].astype(int)
		mask = probabilities[i][5]
		label = probabilities[i][4]
		score = probabilities[i][0]
		image = probabilities[i][6]
		nodule_pbb = float(score) * 100

		draw_box(image, b, color=(255, 255, 0), thickness=1)
		# mask = mask[:, :, label]
		# draw_mask(image, b, mask, color=label_color(label))
		# caption = "{} {:.3f}".format(labels_to_names[label], score)
		# draw_caption(image, b, caption)
		#print("### " + str(slice_index) +".jpg", labels_to_names[label], score)

		buff = BytesIO()
		showImage = Image.fromarray(image).convert("RGBA")
		#showImage = image.resize((700, 450),Image.ANTIALIAS)
		#showImage = trim(image)
		showImage.save(buff, format="PNG")
		img_str = str(base64.b64encode(buff.getvalue()), 'utf-8')

		pre_results.append([slice_index,nodule_pbb,img_str])

	return jsonify({'pre_results':pre_results})


if __name__ == "__main__":
	application.run(host='0.0.0.0')



