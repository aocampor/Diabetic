
INCLUDE += $(shell $(ROOTSYS)/bin/root-config --cflags)
LIBS += $(shell $(ROOTSYS)/bin/root-config --libs)
  
all:	Diabetic_readImages.cpp
	g++ -std=c++0x -pthread -O2 -o readImages Diabetic_readImages.cpp $(INCLUDE) $(LIBS) -ljpeg 

