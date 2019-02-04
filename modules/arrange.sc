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
	bookend:[0,0]
),



initModule: { | self |

},

makeWork: {arg self, workName, eWorkInit=(clock: TempoClock.new);
	// shares settings like tempo clock
	var myW = self.makeModule(workName, eWorkInit);

	self.postln;
	self.makeModule.postln;
	myW.postln;

	// TO DO... rethink these, and use eSomething naming convention
	myW.makeSeqBook = {arg myW, innerItems, eBook;
		var mySeq = [];
		if (eBook.bookend[0] > 0,
			{mySeq = mySeq.add(Pbind(*[note:\rest, dur:Pseq([eBook.bookend[0]])]))}, );
		mySeq = mySeq.addAll(innerItems);
		if (eBook.bookend[1] > 0,
			{mySeq = mySeq.add(Pbind(*[note:\rest, dur:Pseq([eBook.bookend[1]])]))}, );
		Pseq(mySeq);
	};

	myW.makeSeq = {arg myW, namesList, eValues=();
		var eSeqValues = (bookend:[0,0]).putAll(eValues);
		var eItemValues = ().putAll(eValues).putAll((bookend:[0,0] ));
		var myBindList = namesList.collect { arg name; myW[name.asSymbol].bind(eItemValues);  };
		if (eSeqValues.bookend.maxItem > 0,
			{ myW.makeSeqBook(myBindList, eSeqValues); },
			{ Pseq(myBindList); },
		);
	};

	myW.makeBlock = { arg myW, name;
		var myB = myW.makeModule(name);

	};

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

		// TO DO: this changes with TempoClock:
/*		myP.timeLength = {arg myP;
			myP.rhythmLength * (1/myP.bpm) * 60;
		};*/

		myP.playMe = {arg myP, eValues=();
			// TO DO: consider ... simpler to pass protoEvent into play method as
			// opposed to using PbindF below???
			myP.bind(eValues).play(clock:myW.clock);
		};

		// -----------------------------------------------------------------------------------
		myP.bind = {arg myP, eValues=();
			// combines values in iniital event with the new values to be used for the Pbindf:
			// TO DO MAYBE... only pull in the values from the parent event that make sense???
			var eCombo = myP.makeCopy(eValues);
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

			// if a dur value is passed, then replace the rhythm array
			// TO DO: update to work with tempoClock
			// if (eCombo.includesKey('dur') && eCombo.dur.isNil.not,
			// 	{eCombo.rhythm = [eCombo.dur * eCombo.bpm / 60]};
			// );

			// now set the note value to use as a Pser loop through the notes array
			eCombo.note = Pser(eCombo.notes, eCombo.sequenceLength);

			// now set the dur value to use as a Pser loop through the rhythm array
			eCombo.dur = Pser(eCombo.rhythm, eCombo.sequenceLength);

			eCombo.rhythm.postln;

			myBind = Pbindf(myPattern, *eCombo.asPairs); // TO DO... necessary????

			if (eCombo.bookend.maxItem > 0,
				{
					myW.makeSeqBook(myBind, eCombo);
				},
				{ myBind },
			);
		};

	};

	// TO DO ... EVEN NEEDED?
	myW.copyP =  {arg myW, newName, oldName, eValuesNew=();
		var eNew  = myW[oldName.asSymbol].makeCopy(eValuesNew);
		myW.makeP(newName, eNew);
	};
},
)


