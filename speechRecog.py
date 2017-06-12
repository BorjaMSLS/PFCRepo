'''
Created on 12 Jun 2017

@author: NewName01
'''

import speech_recognition as sr
r = sr.Recognizer()
m = sr.Microphone()


try:
    with m as source: r.adjust_for_ambient_noise(source)

    while True:
        with m as source: audio = r.listen(source)
        print("")

    try:

        value = r.recognize_google(audio)

        if str is bytes: 
            result = u"{}".format(value).encode("utf-8")

        else: 
            result = "{}".format(value)

        with open("outputs.txt","a") as f:
            f.write(result)
        print(result)

    except sr.UnknownValueError:
        print("")
    except sr.RequestError as e:
        print("{0}".format(e))
except KeyboardInterrupt:
    pass