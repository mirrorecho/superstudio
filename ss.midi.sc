
(

title: "Basic MIDI Controller Utility",

initModule: { | self |

	MIDIIn.connectAll;

	self.notes = Array.newClear(128);
	self.synthName = "rainPiano";
	self.postNote = false;

	MIDIdef.noteOn(\noteOn, {arg vel, midinote;
		self.notes[midinote] = Synth(self.synthName, [
			\freq, (midinote + rand(0.2) - 0.1).midicps,
			\amp, vel.linlin(0, 127, 0.001, 1.1)
		]);
		if (self.postNote, {("NOTE: " ++ (midinote-60) ++ ", MIDI: " ++ midinote ++ ", FREQ: " ++ midinote.midicps).postln;});
	}
	);

	MIDIdef.noteOff(\noteOff, {arg vel, midinote;
		self.notes[midinote].release; // same as ~noteArray[midinote].set(\gate, 0);
	});

};

)