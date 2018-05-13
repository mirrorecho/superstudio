(

// namespace: ["buf"],
title: "Buffer utilities", // friendly name

libraryPath: "/", // will generally be overwritten

loadLibrary: {arg self, libraryName;
	var postMsgs = [], eLibrary = ();
	SoundFile.collectIntoBuffers(self.libraryPath ++ libraryName ++ "/*").do { arg buffer;
		var bufferName = buffer.path.basename.splitext[0];
		eLibrary[bufferName.asSymbol] = buffer;
		postMsgs = postMsgs.add("Loaded buffer: ~ss.buf['" ++ libraryName ++ "']['" ++ bufferName ++ "']");
	};
	self.makeModule(libraryName, eLibrary);
	self.ss.postPretty(postMsgs);
},

initModule: { | self |
	// TO DO: auto-ability to start from end of buffer
	SynthDef("ss.buf.play", {arg buffer, amp=1.0, rate=1.0, start=0;
		var sig = PlayBuf.ar(2,
			bufnum:buffer,
			rate:BufRateScale.kr(buffer)*rate,
			startPos:BufSampleRate.kr(buffer) * start,
			doneAction:2,
		);
		sig = sig * amp;
		Out.ar(self.ss.bus.master, sig);
	}).add;

	SynthDef("ss.buf.perc", {arg buffer, amp=1.0, rate=1.0, start=0, attackTime=0.01, releaseTime=1, curve= -4;
		var sig = PlayBuf.ar(2,
			bufnum:buffer,
			rate:BufRateScale.kr(buffer)*rate,
			startPos:BufSampleRate.kr(buffer) * start,
			doneAction:2,
		);
		var env = Env.perc(attackTime:attackTime, releaseTime:releaseTime, level:amp, curve:curve);
		sig = sig * amp * EnvGen.ar(env, doneAction: 2);
		Out.ar(self.ss.bus.master, sig);
	}).add;


	SynthDef("ss.buf.drone",{ arg buffer, amp=1.0, rate=1.0,
		// TO DO: implement these:
		startOn=0, endOn=3,
		fadeIn=0.1, sustain=1.0, fadeOut=0.1;

		var length = endOn - startOn;
		var mul = amp; // could do adjustments here...

		var myPlayBuf = PlayBuf.ar(
			numChannels:2,
			bufnum:buffer,
			rate:BufRateScale.kr(buffer)*rate,
			loop:1) * EnvGen.ar(Env.circle([0,1,0], [length/(2*rate), length/(2*rate), 0]));
		Out.ar(self.ss.bus.master,
			// dividing by rate is important to adjust circle to any possible rate...
			DelayN.ar(myPlayBuf, length/(2*rate), length/(2*rate), 1, myPlayBuf)
			* mul
			,0.0);
	}).add;
},

makeSynth: { arg self, synthName, libraryName, bufferName, args=[];
	var buffer, mySynth;
	if (self.includesKey(libraryName.asSymbol), {
		if (self[libraryName.asSymbol].includesKey(bufferName.asSymbol), {
			buffer = self[libraryName.asSymbol][bufferName.asSymbol];
			args.postln;
			mySynth = Synth(synthName, [buffer:buffer]++args);
		}, {
			self.ss.postPretty(["ERROR: cannot play buffer \"" ++ bufferName ++ "\" because it does not exist in the library \"" ++ libraryName ++ "\"."]);
		});
	}, {
		self.ss.postPretty(["ERROR: cannot play buffer in library \"" ++ libraryName ++ "\" because the library has not been loaded."]);
	});
	mySynth;
},


play: { arg self, libraryName, bufferName, args=[];
	args.postln;
	self.makeSynth("ss.buf.play", libraryName, bufferName, *args);
},

drone: { arg self, libraryName, bufferName, args=[];
	self.makeSynth("ss.buf.drone", libraryName, bufferName, args);
},

perc: { arg self, libraryName, bufferName, args=[];
	self.makeSynth("ss.buf.perc", libraryName, bufferName, args);
},



)

