// NAMING CONVENTION NOTE:
// settings are passed as events, with a naming: eMyDescription
// e.g.: eInit, eSeqValues, etc.

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

)