(

// namespace: [], // namespace hierarchy for module
title: "Mastering: Fx Busses and Synths", // friendly name

initModule: { | self |
	{
		/*
		a Function for defining the synth defs, so that its receivier is a Routine, which can wait for
		for the defs to load before calling them...
		*/
		// if (ss.bus.master == nil, { ss.bus.master = Bus.audio(s,2); });
		self.ss.bus.master = Bus.audio(s,2);

		// master out (without routing through master fx)
		// if (ss.bus.masterOut == nil, { ss.bus.masterOut = Bus.audio(s,2); });
		self.ss.bus.masterOut = Bus.audio(s,2);

		s.sync;

		self.ss.postPretty([
			"~ss.bus.master created... send all synth outputs here",
			"do not pass go do not collect $200"
		]);

		SynthDef("ss.masterFx", { arg reverbRoom=0.44, reverbMix=0.2;
			var sig = In.ar(self.ss.bus.master,2);
			sig = FreeVerb2.ar(sig[0], sig[1], room:reverbRoom, mix:reverbMix);
			Out.ar(self.ss.bus.masterOut, sig);
		}).add;

		SynthDef("ss.masterOut", {
			var sig = In.ar(self.ss.bus.masterOut,2);
			sig = Limiter.ar(sig, 0.9);
			Out.ar(0, sig);
		}).add;

		s.sync;

		self.ss.synth.masterOut = Synth("ss.masterOut");
		self.ss.synth.masterFx = Synth("ss.masterFx");

		self.ss.postPretty(["ss.masterFx and ss.masterOut synths created"]);

	}.fork;

},

)

