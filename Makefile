JFLAGS = -g -d classes 
JC = javac
VPATH = classes/
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        enhancedRandom.java \
	generalParams.java \
	landscape.java \
	simData.java \
	simParams.java \
	sParams.java \
	spatialAggregator.java \
	peatlandSimulation.java \
	Peatland.java

peatland.jar: classes
	jar -cfe peatland.jar Peatland -C classes .

default: classes

classes:
	$(CLASSES:.java=.class)

clean:
	$(RM) classes/*.class
