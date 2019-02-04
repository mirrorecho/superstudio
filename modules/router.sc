(

// namespace: [], // namespace hierarchy for module
title: "Router: Fx Busses and Synths", // friendly name

initModule: { | self |
	{

		self.bus = Bus.audio(s,3);

		self.bus1 = Bus.audio(s,2);
		self.bus2 = Bus.audio(s,2);
		self.bus3 = Bus.audio(s,2);
		self.bus4 = Bus.audio(s,2);
		self.bus5 = Bus.audio(s,2);
		self.bus6 = Bus.audio(s,2);
		self.bus7 = Bus.audio(s,2);
		self.bus8 = Bus.audio(s,2);


		s.sync;

		self.ss.postPretty([
			"~ss.bus.master created... send all synth outputs here",
			"do not pass go do not collect $200"
		]);

		SynthDef("ss.router.controlSynth", {
			var sig = In.ar(self.bus,3), x;
			// set x = 3rd channel
			Out.ar(self.ss.bus.master, sig * (((x > 0) + (x < 0)) <=0) );
			Out.ar(self.bus1, sig * (((x > 1) + (x < 1)) <=0) );
			Out.ar(self.bus2, sig * (((x > 2) + (x < 2)) <=0) );
			Out.ar(self.bus3, sig * (((x > 3) + (x < 3)) <=0) );
			Out.ar(self.bus4, sig * (((x > 4) + (x < 4)) <=0) );
			Out.ar(self.bus5, sig * (((x > 5) + (x < 5)) <=0) );
			Out.ar(self.bus6, sig * (((x > 6) + (x < 6)) <=0) );
			Out.ar(self.bus7, sig * (((x > 7) + (x < 7)) <=0) );
			Out.ar(self.bus8, sig * (((x > 8) + (x < 8)) <=0) );
		}).add;

		SynthDef("ss.router.synth1", {
			var sig = In.ar(self.bus1,2);
			Out.ar(self.ss.bus.master, sig);
		}).add;


		s.sync;

		self.controlSynth = Synth("ss.router.controlSynth");
		self.synth1 = Synth("ss.router.synth1");

		self.ss.postPretty(["ss.masterFx and ss.masterOut synths created"]);

	}.fork;

},

)

