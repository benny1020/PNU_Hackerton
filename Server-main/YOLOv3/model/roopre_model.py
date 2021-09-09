import os
from tensorflow import keras
import tensorflow as tf
tf.get_logger().setLevel('ERROR')

import tensorflow_hub as hub
from tensorflow.keras.preprocessing import image
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras import Sequential
from tensorflow.keras.layers import Dropout, Dense
from tensorflow.keras.layers.experimental.preprocessing import Rescaling
#from keras.models import load_model,save_model
import numpy as np
import urllib.request, urllib.error, json

class roopre:
    IMAGESIZE=(224,224)
    INPUTLAYERSIZE=IMAGESIZE + (3,)

    def __init__(self):
        trainingimagesdata = ImageDataGenerator().flow_from_directory("./model/dataset",target_size = (224,224))
        self.num_classes = trainingimagesdata.num_classes
        self.ml_class_names = self.__get_class_lookup(trainingimagesdata)
    def __get_class_lookup(self,training_image_data):
        class_labels = [None]*training_image_data.num_classes
        class_names = training_image_data.class_indices.keys()
        for classname in class_names:
            class_labels[training_image_data.class_indices[classname]] = classname
        return class_labels
    def __define_model(self):
        #print("MLFORKIDS: Defining the layers to include in your neural network")
        model = Sequential([
            # input layer is resizing all images to save having to do that in a manual pre-processing step
            Rescaling(1/127, input_shape=(224, 224, 3)),
            # using an existing pre-trained model as an untrainable main layer
            hub.KerasLayer("https://tfhub.dev/google/imagenet/mobilenet_v2_140_224/classification/4"),
            #
            Dropout(rate=0.2),
            #
            Dense(self.num_classes)
        ])
        model.build((None,) + (224, 224, 3))

        # model compile parameters copied from tutorial at https://www.tensorflow.org/hub/tutorials/tf2_image_retraining
        model.compile(
            optimizer=tf.keras.optimizers.SGD(lr=0.005, momentum=0.9),
            loss=tf.keras.losses.CategoricalCrossentropy(from_logits=True, label_smoothing=0.1),
            metrics=['accuracy'])

        return model

    def train_model(self):
        self.ml_model = self.__define_model()
        if trainingimagesdata.batch_size > trainingimagesdata.samples:
            trainingimagesdata.batch_size = trainingimagesdata.samples
        steps_per_epoch = trainingimagesdata.samples // trainingimagesdata.batch_size
        epochs = 8
        if trainingimagesdata.samples > 55:
            epochs = 30
            #epochs = 1
        self.ml_model.fit(trainingimagesdata, epochs=epochs, steps_per_epoch=steps_per_epoch, verbose=2)
        print("MLFORKIDS: Model training complete")
    def prediction(self, image_location: str):
        if hasattr(self, "ml_model") == False:
            raise RuntimeError("Machine learning model has not been trained for this project")
        testimg = image.load_img(image_location, target_size=(224,224))
        testimg = image.img_to_array(testimg)
        testimg = np.expand_dims(testimg, axis=0)
        predictions = self.ml_model.predict(testimg)
        topprediction = predictions[0]
        topanswer = np.argmax(topprediction)
        return self.ml_class_names[topanswer]

    def md_save(self):
        self.ml_model.save("./model/mymodel")
    def md_load(self):
        self.ml_model = keras.models.load_model("./model/mymodel")
