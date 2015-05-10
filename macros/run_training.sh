#python TrainMVA.py TMVA34.root ../merge/3Fold.root ../merge/4Fold.root 1000000 1000000 4 500
#mv weights/TMVAClassification_MLP.class.C weights/TMVAClassification_MLP.class_34.C
#mv weights/TMVAClassification_MLP.weights.xml weights/TMVAClassification_MLP.weights_34.xml
#
#python TrainMVA.py TMVA23.root ../merge/2Fold.root ../merge/3Fold.root 1000000 1000000 3 500
#mv weights/TMVAClassification_MLP.class.C weights/TMVAClassification_MLP.class_23.C
#mv weights/TMVAClassification_MLP.weights.xml weights/TMVAClassification_MLP.weights_23.xml
#
#python TrainMVA.py TMVA12.root ../merge/1Fold.root ../merge/2Fold.root 1000000 1000000 2 500
#mv weights/TMVAClassification_MLP.class.C weights/TMVAClassification_MLP.class_12.C
#mv weights/TMVAClassification_MLP.weights.xml weights/TMVAClassification_MLP.weights_12.xml

##print 'TrainMVA.py outfile.root background.root signal.root #eventsback #eventsignal $cut $#cycles $#hiddenlayers'
#python TrainMVA.py TMVA01_1_1_1.root ../Output/0.root ../Output/1.root -1 -1 1 1 1
#mv weights/TMVAClassification_MLP.class.C weights/TMVAClassification_MLP.class_01_1_1_1.C
#mv weights/TMVAClassification_MLP.weights.xml weights/TMVAClassification_MLP.weights_01_1_1_1.xml
#
##print 'TrainMVA.py outfile.root background.root signal.root #eventsback #eventsignal $cut $#cycles $#hiddenlayers'
#python TrainMVA.py TMVA01_1_10_1.root ../Output/0.root ../Output/1.root -1 -1 1 10 1
#mv weights/TMVAClassification_MLP.class.C weights/TMVAClassification_MLP.class_01_1_10_1.C
#mv weights/TMVAClassification_MLP.weights.xml weights/TMVAClassification_MLP.weights_01_1_10_1.xml
#
##print 'TrainMVA.py outfile.root background.root signal.root #eventsback #eventsignal $cut $#cycles $#hiddenlayers'
#python TrainMVA.py TMVA01_1_100_1.root ../Output/0.root ../Output/1.root -1 -1 1 100 1
#mv weights/TMVAClassification_MLP.class.C weights/TMVAClassification_MLP.class_01_1_100_1.C
#mv weights/TMVAClassification_MLP.weights.xml weights/TMVAClassification_MLP.weights_01_1_100_1.xml

##print 'TrainMVA.py outfile.root background.root signal.root #eventsback #eventsignal $cut $#cycles $#hiddenlayers'
#python TrainMVA.py TMVA01_1_500_1.root ../merge/0.root ../merge/1.root -1 -1 1 500 1
#mv weights/TMVAClassification_MLP.class.C weights/TMVAClassification_MLP.class_01_1_500_1.C
#mv weights/TMVAClassification_MLP.weights.xml weights/TMVAClassification_MLP.weights_01_1_500_1.xml

#print 'TrainMVA.py outfile.root background.root signal.root #eventsback #eventsignal $cut $#cycles $#hiddenlayers'
python TrainMVA.py TMVA01_1_800_10.root ../merge/0.root ../merge/1.root -1 -1 1 800 10
mv weights/TMVAClassification_MLP.class.C weights/TMVAClassification_MLP.class_01_1_800_10.C
mv weights/TMVAClassification_MLP.weights.xml weights/TMVAClassification_MLP.weights_01_1_800_10.xml

#print 'TrainMVA.py outfile.root background.root signal.root #eventsback #eventsignal $cut $#cycles $#hiddenlayers'
python TrainMVA.py TMVA01_1_800_10.root ../merge/0.root ../merge/1.root -1 -1 1 800 100
mv weights/TMVAClassification_MLP.class.C weights/TMVAClassification_MLP.class_01_1_800_10.C
mv weights/TMVAClassification_MLP.weights.xml weights/TMVAClassification_MLP.weights_01_1_800_10.xml



##print 'TrainMVA.py outfile.root background.root signal.root #eventsback #eventsignal $cut $#cycles $#hiddenlayers'
#python TrainMVA.py TMVA01_1_100_0.root ../Output/0.root ../Output/1.root -1 -1 1 100 0
#mv weights/TMVAClassification_MLP.class.C weights/TMVAClassification_MLP.class_01_1_100_0.C
#mv weights/TMVAClassification_MLP.weights.xml weights/TMVAClassification_MLP.weights_01_1_100_0.xml


##print 'TrainMVA.py outfile.root background.root signal.root #eventsback #eventsignal $cut $#cycles $#hiddenlayers'
#python TrainMVA.py TMVA01_1_100_10.root ../Output/0.root ../Output/1.root -1 -1 1 100 10
#mv weights/TMVAClassification_MLP.class.C weights/TMVAClassification_MLP.class_01_1_100_10.C
#mv weights/TMVAClassification_MLP.weights.xml weights/TMVAClassification_MLP.weights_01_1_100_10.xml
#
##print 'TrainMVA.py outfile.root background.root signal.root #eventsback #eventsignal $cut $#cycles $#hiddenlayers'
#python TrainMVA.py TMVA01_1_100_100.root ../Output/0.root ../Output/1.root -1 -1 1 100 100
#mv weights/TMVAClassification_MLP.class.C weights/TMVAClassification_MLP.class_01_1_100_100.C
#mv weights/TMVAClassification_MLP.weights.xml weights/TMVAClassification_MLP.weights_01_1_100_100.xml


##print 'TrainMVA.py outfile.root background.root signal.root #eventsback #eventsignal $cut $#cycles $#hiddenlayers'
#python TrainMVA.py TMVA01_1_10_0.root ../Output/0.root ../Output/1.root -1 -1 1 10 0
#mv weights/TMVAClassification_MLP.class.C weights/TMVAClassification_MLP.class_01_1_10_0.C
#mv weights/TMVAClassification_MLP.weights.xml weights/TMVAClassification_MLP.weights_01_1_10_0.xml
#
##print 'TrainMVA.py outfile.root background.root signal.root #eventsback #eventsignal $cut $#cycles $#hiddenlayers'
#python TrainMVA.py TMVA01_1_10_1.root ../Output/0.root ../Output/1.root -1 -1 1 10 1
#mv weights/TMVAClassification_MLP.class.C weights/TMVAClassification_MLP.class_01_1_10_1.C
#mv weights/TMVAClassification_MLP.weights.xml weights/TMVAClassification_MLP.weights_01_1_10_1.xml
#
##print 'TrainMVA.py outfile.root background.root signal.root #eventsback #eventsignal $cut $#cycles $#hiddenlayers'
#python TrainMVA.py TMVA01_1_10_10.root ../Output/0.root ../Output/1.root -1 -1 1 10 10
#mv weights/TMVAClassification_MLP.class.C weights/TMVAClassification_MLP.class_01_1_10_10.C
#mv weights/TMVAClassification_MLP.weights.xml weights/TMVAClassification_MLP.weights_01_1_10_10.xml
#
##print 'TrainMVA.py outfile.root background.root signal.root #eventsback #eventsignal $cut $#cycles $#hiddenlayers'
#python TrainMVA.py TMVA01_1_10_100.root ../Output/0.root ../Output/1.root -1 -1 1 10 100
#mv weights/TMVAClassification_MLP.class.C weights/TMVAClassification_MLP.class_01_1_10_100.C
#mv weights/TMVAClassification_MLP.weights.xml weights/TMVAClassification_MLP.weights_01_1_10_100.xml
