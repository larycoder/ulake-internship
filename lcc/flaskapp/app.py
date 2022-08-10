import base64
from io import BytesIO
import os

from PIL import Image
import cv2
from flask import Flask, jsonify
from keras_retinanet.utils.image import preprocess_image, read_image_bgr
from keras_retinanet.utils.visualization import draw_box
import numpy as np
import pandas

from keras_maskrcnn import models
from utils import calculateNoduleSize, nms

application = Flask(__name__)
base = "data"

# TODO: using lake model
model_path = os.path.join(base, 'model/resnet50_csv_07.h5')

if os.path.exists(model_path):
    print(model_path)
else:
    print("No model exist")

# load retinanet model
print(model_path)
model = models.load_model(model_path, backbone_name='resnet50')


@application.route('/detect/<string:patient_id>/')
def detect(patient_id):
    print("Detecting")

    # TODO: do sth in this path
    df_node = pandas.read_csv(base + "/csv/patients3cm.csv")
    df_patient = df_node[df_node["Patient_ID"] == patient_id]
    patient_name = df_patient["Patient_Name"].to_string(index=False)

    if patient_name.startswith(" "):
        patient_name = patient_name.strip()
        patient_name = patient_name.replace(" ", "_")
        print(patient_name)

    # TODO: do something with this path
    k1_prep_result_path = base + '/k_data/K_output_1/'
    k2_prep_result_path = base + '/k_data/K_output_2/'
    if (os.path.isfile(k1_prep_result_path + patient_name + '_clean.npy')):
        image_path = k1_prep_result_path + patient_name + '_clean.npy'
    elif (os.path.isfile(k2_prep_result_path + patient_name + '_clean.npy')):
        image_path = k2_prep_result_path + patient_name + '_clean.npy'
    else:
        print("Not found image file", patient_name)

    img_array = np.load(image_path)
    print("image shape: ", img_array.shape)
    labels_to_names = {0: 'nodule'}
    threshold = 0.5

    test_dir = base + "/test"
    predict_list = []
    for i in range(img_array.shape[0]):
        # pre-process image for network
        cv2.imwrite(test_dir + "/" + str(i) + ".jpg", img_array[i])
        image = read_image_bgr(test_dir + "/" + str(i) + ".jpg")
        pre_image = preprocess_image(image)
        pre_image, scale = resize_image(pre_image)
        image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
        outputs = model.predict_on_batch(np.expand_dims(pre_image, axis=0))
        boxes = outputs[-4][0]
        scores = outputs[-3][0]
        labels = outputs[-2][0]
        masks = outputs[-1][0]

        # correct for image scale
        boxes /= scale
        for box, score, label, mask in zip(boxes, scores, labels, masks):
            # scores are sorted so we can break
            if score < threshold:
                break
            else:
                size = calculateNoduleSize(box[0], box[1], box[2], box[3])
                predict_list.append(
                    [score, int(i), box, size, label, mask, image])
                print(str(i) + ".jpg", i,  labels_to_names[label], score)

    probabilities = nms(predict_list, 0.05)
    print("Lenghth ", len(probabilities))

    pre_results = []
    for i in range(len(probabilities)):
        slice_index = img_array.shape[0] - probabilities[i][1]
        b = probabilities[i][2].astype(int)
        mask = probabilities[i][5]
        label = probabilities[i][4]
        score = probabilities[i][0]
        image = probabilities[i][6]
        nodule_pbb = float(score) * 100
        draw_box(image, b, color=(255, 255, 0), thickness=1)
        buff = BytesIO()
        showImage = Image.fromarray(image).convert("RGBA")
        showImage.save(buff, format="PNG")
        img_str = str(base64.b64encode(buff.getvalue()), 'utf-8')
        pre_results.append([slice_index, nodule_pbb, img_str])

    return jsonify({'pre_results': pre_results})


if __name__ == "__main__":
    application.run(host='0.0.0.0')
