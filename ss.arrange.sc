// NAMING CONVENTION NOTE:
// settings are passed as events, with a naming: eMyDescription
// e.g.: eInit, eSeqValues, etc.

(
title: "Arrangement Tools with Patterns",


initModule: { | self |

},

makeWork: {arg self, workName, eWorkInit=();
	var myW = self.makeModule(workName, (clock:TempoClock.new) ++ eWorkInit);

	myW.protoP = (
		patternType:Pbind,
		playMe: { arg myP, eValues=();
			myP.bind(eValues).play(clock:myW.clock);
		},
		bind: { arg myP, eBindValues=();
			myP.patternType.new(*(myP ++ eBindValues).asPairs);
		},
	);

	myW.getP = {arg myW, eValues=();
		myW.getModule(myW.protoP ++ eValues);
	};

	myW.getMonoP = {arg myW, eValues=();
		myW.getModule(myW.protoP ++ (
			patternType:Pmono, // could also be PmonoArtic
			bind: { arg myP, eBindValues=();
				var myValues = myP ++ eBindValues;
				myP.patternType.new(myValues.instrument, *myValues.asPairs);
			},
		) ++ eValues);
	};

	myW.getBlock = {arg myW, list=[], eValues=();
		myW.getModuleList(list, myW.protoP ++ (
			patternType:Ppar,
			bind:{arg myB, eValues=();
				myB.patternType.new(
					myB.listSize.collect{|i| myB.byIndex(i).bind(eValues[myB.nameList[i]]) };
				);
			};
		) ++ eValues);
	};

	myW.getSeq = {arg myW, list=[], eValues=();
		myW.getBlock(list, eValues) ++ (patternType:Pseq);
	};

	// for making modules underneath the work....

	myW.makeP = {arg myW, name, eValues=();
		myW.makeModule(name, myW.getP(eValues));
	};

	myW.makeMonoP = {arg myW, name, eValues=();
		myW.makeModule(name, myW.getMonoP(eValues));
	};

	myW.makeBlock = {arg myW, name, list, eValues=();
		myW.makeModule(name, myW.getBlock(list, eValues));
	};

	myW.makeSeq = {arg myW, name, list, eValues=();
		myW.makeModule(name, myW.getSeq(list, eValues));
	};


	myW;

},
)


