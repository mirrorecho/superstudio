
(

// namespace:[],
title: "Basic MIDI Controller Utility",

initModule: { | self, ss |

	MIDIIn.connectAll;

	self.notes = Array.newClear(128);
	self.synthName = "ss.spacey";

	MIDIdef.noteOn(\noteOn, {arg vel, midinote;
		self.notes[midinote] = Synth(self.synthName, [
			\freq, (midinote + rand(0.2) - 0.1).midicps,
			\amp, vel.linexp(0, 127, 0.01, 0.69)
		]);
	}
	);

	MIDIdef.noteOff(\noteOff, {arg vel, midinote;
		self.notes[midinote].release; // same as ~noteArray[midinote].set(\gate, 0);
	});

};

)