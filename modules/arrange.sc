// NAMING CONVENTION NOTE:
// settings are passed as events, with a naming: eMyDescription
// e.g.: eInit, eSeqValues, etc.


(
title: "Arrangement Tools with Patterns",
ePatternDefault: (eventMax:inf, repeats:1, rhythm:[1], notes:[0], bookend:[0,0]),
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
			{mySeq = mySeq.add(Pbind(*[note:\rest, dur:Pseq([eBook.bookend[0] * myW.clock.tempo])]))}, );
		mySeq = mySeq.addAll(innerItems);
		if (eBook.bookend[1] > 0,
			{mySeq = mySeq.add(Pbind(*[note:\rest, dur:Pseq([eBook.bookend[1] * myW.clock.tempo])]))}, );
		Pseq(mySeq);
	};

	myW.makeSeq = {arg myW, namesList, eValues=();
		var eItemValues = eValues.putAll((bookend:[0,0] ));
		var myBindList = namesList.collect { arg name; myW[name.asSymbol].bind(eItemValues);  };
		if (eValues.bookend.maxItem > 0,
			{ myW.makeSeqBook(myBindList, eValues); },
			{ Pseq(myBindList); },
		);
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
		myP.timeLength = {arg myP;
			myP.rhythmLength * (1/myP.bpm) * 60;
		};

		myP.settingsWith = {arg myP, eSettings=();
			// returns a new pattern event with settings overriden
			().putAll(myP).putAll(eSettings);
		};

		myP.playMe = {arg myP, eValues=();
			// TO DO: consider ... simpler to pass protoEvent into play method as
			// opposed to using PbindF below???
			myP.bind(eValues).play(clock:myW.clock);
		};

		myP.playMeMono = {arg myP, eValues=();
			// TO DO: consider ... ditto
			myP.bindMono(eValues).play(clock:myW.clock);
		};

		myP.bind = {arg myP, eValues=();
			myP.makeBind(eValues, Pbind());
		};

		myP.bindMono = {arg myP, eValues=();
			myP.makeBind(eValues, PmonoArtic());
		};

		// -----------------------------------------------------------------------------------
		myP.makeBind = {arg myP, eValues=(), pattern;
			// combines values in iniital event with the new values to be used for the Pbindf:
			// TO DO MAYBE... only pull in the values from the parent event that make sense???
			var eCombo = myP.settingsWith(eValues);
			var myBind;

			// if a note value is passed, then replace array of notes
			if (eCombo.includesKey('note') && eCombo.note.isNil.not,
				{eCombo.notes = [eCombo.note];});

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

			myBind = Pbindf(pattern, *eCombo.asPairs);
			if (eCombo.bookend.maxItem > 0,
				{
					myW.makeSeqBook(myBind, eCombo);
				},
				{ myBind },
			);
		};

	};

	myW.copyP =  {arg myW, newName, oldName, ePatternNew=();
		var eNew  = myW[oldName.asSymbol].settingsWith(ePatternNew);
		myW.makeP(newName, eNew);
	};
},
)


/*
(
~ss.makeModule("arrange", ["arrange"], "Arrangement Tools with Patterns", { arg ss, module;
	module.bpm = 60;
	module.eDefault = (eventMax:inf, repeats:1, rhythm:[1], notes:[0], bookendRests:[0,0]);

	// ----------------------------------------------------------------------------------------------
	module.makeSeqBook = {arg env, innerItems, sequenceSettings;
		var mySeq = [];
		var bookSettings = module.settingsWith(sequenceSettings);
		if (bookSettings.bookendRests[0] > 0,
			{mySeq = mySeq.add(Pbind(*[note:\rest, dur:Pseq([bookSettings.bookendRests[0] * bookSettings.beatTime])]))}, );
		mySeq = mySeq.addAll(innerItems);
		if (bookSettings.bookendRests[1] > 0,
			{mySeq = mySeq.add(Pbind(*[note:\rest, dur:Pseq([bookSettings.bookendRests[1] * bookSettings.beatTime])]))}, );
		Pseq(mySeq);
	};

	module.makeSeq = {arg env, namesList, valuesEvent=();
		var sequenceItemSettings = valuesEvent.putAll((bookendRests:[0,0] ));
		var myBindList = namesList.collect { arg name; env[name.asSymbol].bind(sequenceItemSettings);  };
		if (valuesEvent.bookendRests.maxItem > 0,
			{ module.makeSeqBook(myBindList, valuesEvent); },
			{ Pseq(myBindList); },
		);
	};

	module.settingsWith = {arg env, valuesEvent=(), initEvent=module.eDefault;
		var settingsEvent = (bpm:env.bpm).putAll(initEvent).putAll(valuesEvent);

		settingsEvent.sequenceLength = [
			settingsEvent.eventMax, [
				settingsEvent.rhythm.size * settingsEvent.repeats, settingsEvent.notes.size * settingsEvent.repeats,
			].maxItem
		].minItem;


		settingsEvent.rhythm_length = {arg mySettings;
			sum( mySettings.sequenceLength.collect{ |i| mySettings.rhythm[i % mySettings.rhythm.size]; } );
		};

		settingsEvent.time_length = {arg mySettings;
			mySettings.rhythm_length * (1/mySettings.bpm) * 60;
		};

		settingsEvent.beatTime = (1/settingsEvent.bpm) * 60;

		settingsEvent;
	};

	// ----------------------------------------------------------------------------------------------
	module.makeP =  {arg env, name, initEvent=();
		// TO DO... able to pass in more sophisticated values for rhythm/notes as opposed to simple arrays
		var myP;
		module[name.asSymbol] = Environment.make;
		myP = module[name.asSymbol];

		myP.know = true;
		// TO DO... do we need this child event for settings?
		myP.settings = module.settingsWith(initEvent);

		myP.settingsWith = {arg env, valuesEvent=();
			module.settingsWith(valuesEvent, myP.settings);
		};

		// ----------------------------------------------------------------------------------------------
		myP.makeBind = {arg env, name, valuesEvent=(), pattern;
			// combines values in iniital event with the new values to be used for the Pbindf:
			// TO DO MAYBE... only pull in the values from the parent event that make sense???
			var comboEvent = myP.settingsWith(valuesEvent);
			var myBind;


			// if a note value is passed, then replace array of notes
			if (comboEvent.includesKey('note') && comboEvent.note.isNil.not,
				{comboEvent.notes = [comboEvent.note];});

			// if a dur value is passed, then replace the rhythm array
			if (comboEvent.includesKey('dur') && comboEvent.dur.isNil.not,
				{comboEvent.rhythm = [comboEvent.dur * comboEvent.bpm / 60]};
			);

			// now set the note value to use as a Pser loop through the notes array
			comboEvent.note = Pser(comboEvent.notes, comboEvent.sequenceLength);

			// now set the dur value to use as a Pser loop through the rhythm array
			comboEvent.dur = Pser(comboEvent.rhythm, comboEvent.sequenceLength) * comboEvent.beatTime;

			myBind = Pbindf(pattern, *comboEvent.asPairs);
			if (comboEvent.bookendRests.maxItem > 0,
				{
					module.makeSeqBook(myBind, comboEvent);
				},
				{ myBind },
			);
		};


		myP.bind = {arg env, valuesEvent=();
			myP.makeBind(name, valuesEvent, Pbind());
		};

		myP.bindMono = {arg env, valuesEvent=();
			myP.makeBind(name, valuesEvent, PmonoArtic());
		};


		myP.rhythm_length = {arg env;
			myP.settingsWith().rhythm_length;
		};

		// DITTO WARNING AS ABOVE
		myP.time_length = {arg env;
			myP.settingsWith().time_length;
		};

	};

	// ----------------------------------------------------------------------------------------------
	module.copyP =  {arg env, newName, oldName, initEvent=();

		var oldSettings = env[oldName.asSymbol].settings;
		var newSettings = ().putAll(oldSettings).putAll(initEvent);
		module.makeP(newName, newSettings);

	};

});

)*/