
(

title: "Basic MIDI Controller Utility",

defaultCcMapping: ( // Radium
	1:  \mod,
	82: \slide1,
	83: \slide2,
	28: \slide3,
	29: \slide4,
	16: \slide5,
	80: \slide6,
	18: \slide7,
	19: \slide8,
	74: \knob9,
	71: \knob10,
	81: \knob11,
	91: \knob12,
	2:  \knob13,
	10: \knob14,
	5:  \knob15,
	21: \knob16,
	7:  \slideData
),

currentCc: (),

ccFunc: {arg self, ccKey, msgValue, msgNum, chan, src; },

noteOnFunc: {arg self, vel, midinote; },

noteOffFunc: {arg self, vel, midinote; },

initModule: { | self |

	self.ccMapping = self.defaultCcMapping; // TO DO: donsider allowing override;

	self.ccMapping.do{ arg ccKey;
		self.currentCc[ccKey] = 0;
	};

	MIDIIn.connectAll;

	MIDIdef.cc(\listener,  {arg msgValue, msgNum, chan, src;
		var ccKey = self.ccMapping[msgNum];
		self.currentCc[ccKey] = msgValue;
		self.ccFunc(ccKey, msgValue, msgNum, chan, src);
	});


	self.notes = Array.newClear(128);
	self.synthName = "sampledPiano";
	self.postNote = false;

	MIDIdef.noteOn(\noteOn, {arg vel, midinote;
		self.notes[midinote] = Synth(self.synthName, [
			\freq, (midinote + rand(0.2) - 0.1).midicps,
			\amp, vel.linlin(0, 127, 0.001, 1.1)
		]);
		if (self.postNote, {("NOTE: " ++ (midinote-60) ++ ", MIDI: " ++ midinote ++ ", FREQ: " ++ midinote.midicps).postln;});
		self.noteOnFunc(vel, midinote);
	}
	);

	MIDIdef.noteOff(\noteOff, {arg vel, midinote;
		self.notes[midinote].release; // same as ~noteArray[midinote].set(\gate, 0);
		self.noteOffFunc(vel, midinote);
	});

};

)




