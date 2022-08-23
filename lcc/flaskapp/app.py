import base64
from io import BytesIO
import io
import os
import sys

from PIL import Image
import cv2
from flask import Flask, jsonify
from keras_retinanet.utils.image import preprocess_image, read_image_bgr
from keras_retinanet.utils.visualization import draw_box
import numpy as np
import tensorflow as tf
from tensorflow.python.keras.backend import set_session

from keras_maskrcnn import models
from utils import calculateNoduleSize, nms, resize_image


application = Flask(__name__)

base = os.environ["LCC_BASE"]
data = os.environ["LCC_DATA"]
if base is None:
    print("Could not detect lcc base")
    sys.exit(1)
elif data is None:
    print("Could not detect lcc data")
    sys.exit(1)

# Preserve variable for holding model
model = None
graph = None
sess = tf.Session()


class Resp:
    @staticmethod
    def build(code, msg=None, resp=None):
        return jsonify({"code": code, "msg": msg, "resp": resp})


def setup_model(path):
    # load retinanet model
    global model, graph, sess

    set_session(sess)
    model_path = os.path.join(base, path)
    model = models.load_model(model_path, backbone_name="resnet50")
    graph = tf.get_default_graph()


@application.route('/detect/<string:patient_file_id>')
def detect(patient_file_id):
    global model, graph, sess

    if model is None:
        return Resp.build(404, "Model is not setup")

    print("Detecting...")
    image_path = os.path.join(data, patient_file_id)
    img_array = np.load(image_path)

    print("image shape: ", img_array.shape)
    labels_to_names = {0: 'nodule'}
    threshold = 0.5

    predict_list = []
    for i in range(img_array.shape[0]):
        # pre-process image for network
        _, buff = cv2.imencode(".jpg", img_array[i])
        io_buff = io.BytesIO(buff)

        image = read_image_bgr(io_buff)
        pre_image = preprocess_image(image)
        pre_image, scale = resize_image(pre_image)
        image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

        set_session(sess)
        with graph.as_default():
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

    return Resp.build(200, "", pre_results)


if __name__ == "__main__":
    setup_model("model/resnet50_csv_07.h5")
    application.run(host='0.0.0.0')
