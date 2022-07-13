from flask import Flask
from flask import render_template
import csv
import pandas
from pager import Pager
import numpy as np
import base64
from PIL import Image, ImageFont, ImageDraw, ImageEnhance
from io import BytesIO

import io
import cv2

import matplotlib.pyplot as plt
import requests

app = Flask(__name__)

def get_csv(csv_path):
    csv_file = open(csv_path, 'r')
    csv_obj = csv.DictReader(csv_file)
    csv_list = list(csv_obj)
    return csv_list

@app.route('/service/patients')
def patient_table():
#    object_list = get_csv('/var/www/FlaskApp/FlaskApp/candidate_list.csv')
    object_list = get_csv('/var/www/FlaskApp/FlaskApp/list_patient.csv')
    return render_template("patient_table.html",object_list=object_list)


@app.route('/service/detection/<string:candidate_name>/<int:index>/')
def detail(candidate_name,index=None):

#	object_list = get_csv('/var/www/FlaskApp/FlaskApp/list_patient.csv')
#    	for row in object_list:
#	        if row['Patient_Name'] == candidate_name:
#            		last3Id = row['ID'][-3:]
#            		candidate_name = candidate_name.replace(" ", "_")
#            		patient_name_id = candidate_name+last3Id
#            		print(patient_name_id)

	#curl http://192.168.0.222:8999/NGUYEN_BA_SINH/
	url = 'http://192.168.0.222:8999/' + candidate_name +'/'
	r = requests.get(url)
	data = r.json()
	prediction = data['pre_results']
#	slice_index = data['pre_results'][0]
#	nodule_pbb = data['pre_results'][1]

	template = 'patient_detection.html'
	return render_template(template,prediction=prediction)

#	return candidate_name + url 


#    get_prediction = pandas.read_csv("predicted_results/prediction.csv")
#    df_pbb = get_prediction[get_prediction["patient_name"] == candidate_name]
#    pbb = df_pbb.values[0]
#    probability = pbb[0] * 100

#    results = pandas.read_csv("predicted_results/nodule_detection.csv")
#    df_patient = results[results["patient_name"] == candidate_name]


#    pager = Pager(len(df_patient))
#    pager.current = index
#    if index >= pager.count:
#        return render_template("404.html"), 404
#    else:

#        nodule_annos = []
#        for idx, annotation in df_patient.iterrows():
#            nodule_pbb = float(annotation["malscore"]) * 10
#            if nodule_pbb > 100:
#                nodule_pbb = 100

#            nodule_pbb = abs(nodule_pbb)

#            imgFile = np.load("predicted_results/processed_data/" + annotation["patient_name"] + "_clean.npy")

#            imgFile = imgFile[0, int(annotation["coord_z"])]
#            img = Image.fromarray(imgFile).convert("RGBA")


#            draw = ImageDraw.Draw(img)
#            draw.rectangle(((int(annotation["coord_y"]) - int(annotation["diameter"]), int(annotation["coord_x"]) - int(annotation["diameter"]),
#                            (int(annotation["coord_y"]) + int(annotation["diameter"]), int(annotation["coord_x"]) + int(annotation["diameter"])))), outline="red")

#            buff = BytesIO()
#            img = img.resize((1680, 800),Image.ANTIALIAS)
#            img.save(buff, format="PNG")
#            img_str = base64.b64encode(buff.getvalue())
#
#            nodule_annos.append([img_str,nodule_pbb,annotation["diameter"],annotation["coord_z"]])


#        return render_template(template,nodule_annos=nodule_annos)

if __name__ == '__main__':
    app.run(debug=True)
