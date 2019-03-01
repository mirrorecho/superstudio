(

title: "Mastering: Fx Busses and Synths",

initModule: { | self |
	{

		~ss.bus.makeBus("master");
		~ss.bus.makeBus("masterOut");

		s.sync;

		~ss.postPretty([
			"~ss.bus.master created... send all synth outputs here",
			"do not pass go do not collect $200"
		]);

		SynthDef("masterFx", { arg reverbRoom=0.44, reverbMix=0.2;
			var sig = In.ar(~ss.bus.master,2);
			sig = FreeVerb2.ar(sig[0], sig[1], room:reverbRoom, mix:reverbMix);
			Out.ar(~ss.bus.masterOut, sig);
		}).add;

		SynthDef("masterOut", {
			var sig = In.ar(~ss.bus.masterOut,2);
			sig = Limiter.ar(sig, 0.9);
			Out.ar(0, sig);
		}).add;

		s.sync;

		// masterOut synth has to be created first before masterFx... WHY?
		~ss.synther.makeSynth("masterOut");

		s.sync;

		~ss.synther.makeSynth("masterFx");

	}.fork;

},

)

