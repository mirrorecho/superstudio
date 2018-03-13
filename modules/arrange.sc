// TO DO: an equivalent for PmonoArtic
// .... in particular, being able to swith between the two... (i.e. wiith same original settings)
// TO DO: arrange should be work-specific (namespaced to work... not general to ss)
// TO DO: consider using PatternConductor...
// TO DO... also consider using built-in TempoClock instead of my own custom bpm-based settings

// TO DO: tools for rates/ etc. for buffers

(
~ss.makeModule("arrange", ["arrange"], "Arrangement Tools with Patterns", { arg ss, module;
	module.bpm = 60;
	module.defaultSettings = (eventMax:inf, repeats:1, rhythm:[1], notes:[0], bookendRests:[0,0]);


	// ----------------------------------------------------------------------------------------------
	module.makeBind = {arg env, name, valuesEvent=();
		// combines values in iniital event with the new values to be used for the Pbindf:
		// TO DO MAYBE... only pull in the values from the parent event that make sense???
		var comboEvent = (bpm:module.bpm).putAll(env[name.asSymbol].settings).putAll(valuesEvent);
		var sequenceLength, myBind;

		comboEvent.beatTime = (1/comboEvent.bpm) * 60;

		// if a note value is passed, then replace array of notes
		if (comboEvent.includesKey('note') && comboEvent.note.isNil.not,
			{comboEvent.notes = [comboEvent.note];});

		// if a dur value is passed, then replace the rhythm array
		if (comboEvent.includesKey('dur') && comboEvent.dur.isNil.not,
			{comboEvent.rhythm = [comboEvent.dur * comboEvent.bpm / 60]};
		);

		sequenceLength = [
			comboEvent.eventMax, [
				comboEvent.rhythm.size * comboEvent.repeats, comboEvent.notes.size * comboEvent.repeats,
			].maxItem
		].minItem;

		// now set the note value to use as a Pser loop through the notes array
		comboEvent.note = Pser(comboEvent.notes, sequenceLength);

		// now set the dur value to use as a Pser loop through the rhythm array
		comboEvent.dur = Pser(comboEvent.rhythm, sequenceLength) * comboEvent.beatTime;

		myBind = Pbindf(env.pattern, *comboEvent.asPairs);
		if (comboEvent.bookendRests.maxItem > 0,
			{
				var mySeq = [];
				// comboEvent.bookendRests[0] * comboEvent.beatTime.postln;
				if (comboEvent.bookendRests[0] > 0,
					{mySeq = mySeq.add(Pbind(*[note:\rest, dur:Pseq([comboEvent.bookendRests[0] * comboEvent.beatTime])]))}, );
				mySeq = mySeq.add(myBind);
				if (comboEvent.bookendRests[1] > 0,
					{mySeq = mySeq.add(Pbind(*[note:\rest, dur:Pseq([comboEvent.bookendRests[1] * comboEvent.beatTime])]))}, );
				// mySeq.postln;
				Pseq(mySeq);
			},
			{ myBind },
		);
	};

	// ----------------------------------------------------------------------------------------------
	module.makeP =  {arg env, name, initEvent=(), mono;

		// TO DO... able to pass in more sophisticated values for rhythm/notes as opposed to simple arrays

		module[name.asSymbol] = Environment.make;
		module[name.asSymbol].know = true;
		module[name.asSymbol].settings = ().putAll(env.defaultSettings).putAll(initEvent);
		module[name.asSymbol].pattern = Pbind();


		module[name.asSymbol].bind = {arg env, valuesEvent=();
			module.makeBind(name, valuesEvent);
		};

		// Re-ADD IF USEFUL:
/*		module[name.asSymbol].rhythm_length = {arg env;
			sum(env.settings.rhythm);

		module[name.asSymbol].time_length = {arg env;
			env.rhythm_length * (1/env.settings.bpm) * 60;};
		};*/


	};

	// ----------------------------------------------------------------------------------------------
	// TO DO: DRY HERE: TO MUCH IS REPEATED BETWEN makeP and makePmono... DRY..!!!
	module.makePmono =  {arg env, name, initEvent=();

		module[name.asSymbol] = Environment.make;
		module[name.asSymbol].know = true;
		module[name.asSymbol].settings = ().putAll(env.defaultSettings).putAll(initEvent);
		module[name.asSymbol].pattern = PmonoArtic();


		module[name.asSymbol].bind = {arg env, valuesEvent=();
			module.makeBind(name, valuesEvent);
		};
	};


	// ----------------------------------------------------------------------------------------------
	module.copyP =  {arg env, newName, oldName, initEvent=();

		var oldSettings = env[oldName.asSymbol].settings;
		var newSettings = ().putAll(oldSettings).putAll(initEvent);
		module.makeP(newName, newSettings);

	};

	// ----------------------------------------------------------------------------------------------
	module.makeSeq = {arg env, mySequence, valuesEvent=();
		Pseq( mySequence.collect { arg name; env[name.asSymbol].bind(valuesEvent);  } );
	};

});

)