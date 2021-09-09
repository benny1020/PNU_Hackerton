#import food_classifier_yolo as fc
from food_classifier_yolo import *
import cv2
from flask import Flask, request
import numpy as np
import os, glob, numpy as np
import json
from mlforkids import MLforKidsImageProject
from model.roopre_model import *


rp = roopre()
rp.md_load()

def food_pred(img_name):
    #rp.prediction(img_name)
    return rp.prediction(img_name)

def food_pred_json(image):
    # do somthing
    #print(args.showText)
    locations = food_classifier_pipeline(frame=image) #[(2321, 0, 0, 10, 10)] # list of (id, rect) from classfication
    jsons = []
    x1_arr = []
    y1_arr = []
    x2_arr = []
    y2_arr = []
    name = []
    num=0
    for j,location in enumerate(locations):
        class_id, x, y, width, height, framewidth, frameheight =location
        res_json = {}
        #res_json["ClassID"] = classes_codes[class_id] # code , class_id (training class)
        #name.append(classes[class_id])
        x1=x
        y1=y
        x2 = int(x)+int(width)
        y2 = int(y)+int(height)
        if(x < 0):
            x=0
            x1=0
        if(y<0):
            y=0
            y1=0
        if(int(x)+int(width) >= framewidth):
            x2_arr.append(framewidth)
            x2=framewidth
        else:
            x2_arr.append(int(x)+int(width))
            x2=x+width

        if(int(y)+int(height) >= frameheight):
            y2_arr.append(frameheight)
            y2=frameheight
        else:
            y2_arr.append(int(y)+int(height))
            h2=int(y)+int(height)

        x1_arr.append(int(x))
        y1_arr.append(int(y))
        #x2_arr.append(int(x)+int(width))
        #y2_arr.append(int(y)+int(height))
        num+=1
        tmp = image[y1:y2,x1:x2]
        cv2.imwrite("tmp.jpeg",tmp)
        nametmp = food_pred("tmp.jpeg")
        cv2.imwrite(nametmp+str(int(x))+".jpeg")
        print(nametmp+" 으로 예측")
        name.append(nametmp)
        #print(classes[class_id],class_id)
        #res_json["w"] = int(width)
        #res_json["h"] = int(height)
        #res_json["framewidth"] = int(framewidth)
        #res_json["frameheight"]= int(frameheight)
        #jsons.append(res_json)
    data = {
        "x1_arr" : x1_arr,
        "y1_arr" : y1_arr,
        "x2_arr" : x2_arr,
        "y2_arr" : y2_arr,
        "name" : name,
        "num" : num
    }
    js = json.dumps(data)
    return js

app = Flask(__name__)
@app.route('/',methods = ['GET','POST'])

def image_predict():
    if request.method == 'POST':
        f = request.files['file']
        f.save("./Android/image.jpeg")
        image = cv2.imread("./Android/image.jpeg")
        js = food_pred_json(image)
        #js = fc.food_classifier_Json(image)
        #data = {"x1_arr": [525, 563, 125], "y1_arr": [670, 112, 434], "x2_arr": [1269, 1168, 444], "y2_arr": [1134, 611, 864], "name": ["\ub77c\uba74", "\uae40\uce58\ubcf6\uc74c\ubc25", "\ub3c8\uae4c\uc2a4"], "num": 3}
        #j = json.dumps(data)
        #return j
        print(js)
        return js

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=7777,debug=True)
