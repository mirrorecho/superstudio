// NAMING CONVENTION NOTE:
// settings are passed as events, with a naming: eMyDescription
// e.g.: eInit, eSeqValues, etc.

(
title: "Arrangement Tools with Patterns",

ePatternDefault: (
	patternType:Pbind, // could also be Pmono or PmonoArtic
	eventMax:inf,
	repeats:1,
	rhythm:[1],
	notes:[0],
	// bookend:[0,0] // TO DO: implement bookend rests
),



initModule: { | self |

},

makeWork: {arg self, workName, eWorkInit=(clock: TempoClock.new);
	var myW = self.makeModule(workName, eWorkInit);

	myW.makeBlock = { arg myW, name, list=[], eValues=();
		var myB = myW.makeModuleList(name, list, eValues);

		myB.patternType = Ppar;

		myB.bind = {arg myB, eValues=();
			myB.patternType.new(
				myB.listSize.collect{|i| myB.byIndex(i).bind(eValues[myB.nameList[i]]) };
			);
		};

		myB.playMe = {arg myB, eValues=();
			myB.bind(eValues).play(clock:myW.clock);
		};
	};

	myW.makeSeq = { arg myW, name, list=[], eValues=();
		var myS = myW.makeBlock(name, list, eValues);
		myS.patternType = Pseq;
		myS;
	};

	// TO DO: makeP ?? funciton name OK?
	myW.makeP = {arg myW, name, ePatternInit=();
		var myP = myW.makeModule(name, self.ePatternDefault);

		myP.putAll(ePatternInit);

		myP.sequenceLength = {arg myP;
			[ myP.eventMax,
				[ myP.rhythm.size * myP.repeats, myP.notes.size * myP.repeats, ].maxItem
			].minItem;
		};

		myP.rhythmLength = {arg myP;
			sum( myP.sequenceLength.collect{ |i| myP.rhythm[i % myP.rhythm.size]; } );
		};

		// TO DO: IMPLEMENT timeLength (needs to work with TempoClock):
		/* myP.timeLength = {arg myP;
			myP.rhythmLength * (1/myP.bpm) * 60;
		};*/

		myP.playMe = {arg myP, eValues=();
			myP.bind(eValues).play(clock:myW.clock);
		};

		// -----------------------------------------------------------------------------------
		myP.bind = {arg myP, eValues=();
			// combines values in iniital event with the new values to be used for the Pbindf:
			// TO DO MAYBE... only pull in the values from the parent event that make sense???
			var eCombo = myP.getCopy(myP.name, eValues);
			var myBind;
			var myPattern;

			// for Pmono and PmonoArtic, instrument should be specified as arg
			if ( eCombo.patternType==Pmono || eCombo.patternType==PmonoArtic,
				{ myPattern = eCombo.patternType.new(eCombo.instrument); }, // NOTE: new method must be used explicitly when variable is a type...
				{ myPattern = eCombo.patternType.new(); }
			);

			// if a note value is passed, then replace array of notes
			if (eCombo.includesKey('note') && eCombo.note.isNil.not,
				{eCombo.notes = [eCombo.note];}
			);

			// TO DO: CONSIDER IMPLEMENTING THE FOLLOWING:
			// // if a dur value is passed, then replace the rhythm array
			// // TO DO: update to work with tempoClock
			// // if (eCombo.includesKey('dur') && eCombo.dur.isNil.not,
			// // 	{eCombo.rhythm = [eCombo.dur * eCombo.bpm / 60]};
			// // );

			// now set the note value to use as a Pser loop through the notes array
			eCombo.note = Pser(eCombo.notes, eCombo.sequenceLength);

			// now set the dur value to use as a Pser loop through the rhythm array
			eCombo.dur = Pser(eCombo.rhythm, eCombo.sequenceLength);

			myBind = Pbindf(myPattern, *eCombo.asPairs); // TO DO... necessary????

		};

	};
	myW;

},
)


