import base64
from io import BytesIO
import os
import sys
import time

from PIL import Image
import cv2
from flask import Flask, jsonify
from keras_retinanet.utils.image import preprocess_image, read_image_bgr
from keras_retinanet.utils.visualization import draw_box
import numpy as np
import tensorflow as tf

from keras_maskrcnn import models
from utils import calculateNoduleSize, nms

application = Flask(__name__)

base = os.environ["LCC_BASE"]
if base is None:
    print("Could not detect lcc base")
    sys.exit(1)

# Preserve variable for holding model
class Model:
    _model = None
    _flag = "idle"

    @staticmethod
    def get_instance():
        while Model._flag in ["in-load", "in-shutdown"]:
            time.sleep(5)
        return Model._model


@application.route('/health')
def health_check():
    return jsonify({
        "code": 200,
        "base": base,
        "model": str(Model.get_instance()),
        "flag": Model._flag
    })


@application.route('/reset')
def reset_model():
    if Model.get_instance() is not None:
        Model._flag = "in-shutdown"
        tf.keras.backend.clear_session()
        Model._model = None
        Model._flag = "idle"
    return jsonify({"code": 200})


@application.route('/setup/<string:model_file_id>')
def setup_model(model_file_id):
    # load retinanet model
    model_path = os.path.join(base, model_file_id)
    Model.get_instance() # wait for another process finish
    Model._flag = "in-load"
    Model._model = models.load_model(model_path, backbone_name="resnet50")
    Model._flag = "running"
    return jsonify({"code": 200})


@application.route('/detect/<string:patient_file_id>')
def detect(patient_file_id):
    global model
    if Model.get_instance() is None:
        return jsonify({"code": 404, "msg": "Model is not setup"})
    else:
        model = Model.get_instance()

    print("Detecting...")
    image_path = os.path.join(base, patient_file_id)
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

    return jsonify({"pre_results": pre_results, "code": 200})


if __name__ == "__main__":
    application.run(host='0.0.0.0')
