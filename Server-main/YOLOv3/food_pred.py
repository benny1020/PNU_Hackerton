from mlforkids import MLforKidsImageProject

# treat this key like a password and keep it secret!
key = "24fe8030-095d-11ec-997a-71bd17f5251902a8470f-a6f6-4986-8dc8-865d1552dbb4"

# this will train your model and might take a little while
myproject = MLforKidsImageProject(key)
myproject.train_model()

# CHANGE THIS to the image file you want to recognize

print("pred start-----------------------")
demo = myproject.prediction("gimchi.jpeg")

label = demo["class_name"]
confidence = demo["confidence"]

# CHANGE THIS to do something different with the result
print ("result: '%s' with %d%% confidence" % (label, confidence))
