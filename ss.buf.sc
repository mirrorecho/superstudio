(

// namespace: ["buf"],
title: "Buffer utilities", // friendly name

libraryPath: "/", // will generally be overwritten

initModule: { | self |
	// TO DO: auto-ability to start from end of buffer
	SynthDef(
		"bufPlay", {arg bufnum, amp=1.0, rate=1.0, start=0, out=~ss.bus.master;
		var sig = PlayBuf.ar(2,
			bufnum:bufnum,
			rate:BufRateScale.kr(bufnum)*rate,
			startPos:BufSampleRate.kr(bufnum) * start,
			doneAction:2,
		);
		sig = sig * amp;
		Out.ar(out, sig);
	}).add;

	SynthDef("bufPerc", {
		arg bufnum, amp=1.0, rate=1.0, start=0, attackTime=0.001, releaseTime=1, curve= -4, out=~ss.bus.master;
		var sig = PlayBuf.ar(2,
			bufnum:bufnum,
			rate:BufRateScale.kr(bufnum)*rate,
			startPos:BufSampleRate.kr(bufnum) * start,
			doneAction:2,
		);
		var env = Env.perc(attackTime:attackTime, releaseTime:releaseTime, level:amp, curve:curve);
		sig = sig * amp * EnvGen.ar(env, doneAction: 2);
		Out.ar(out, sig);
	}).add;


	SynthDef("bufDrone",{
		arg bufnum, amp=1.0, rate=1.0, out=~ss.bus.master,
		// TO DO: implement these:
		startOn=0, endOn=3,
		fadeIn=0.1, sustain=1.0, fadeOut=0.1;

		var length = endOn - startOn;
		var mul = amp; // could do adjustments here...

		var myPlayBuf = PlayBuf.ar(
			numChannels:2,
			bufnum:bufnum,
			rate:BufRateScale.kr(bufnum)*rate,
			loop:1) * EnvGen.ar(Env.circle([0,1,0], [length/(2*rate), length/(2*rate), 0]));
		Out.ar(out,
			// dividing by rate is important to adjust circle to any possible rate...
			DelayN.ar(myPlayBuf, length/(2*rate), length/(2*rate), 1, myPlayBuf)
			* mul
			,0.0);
	}).add;

	SynthDef("bufSwell", {
		arg bufnum, amp=1.0, rate=1.0, start=0, dur=1, releaseTime=0.01, curve=4, tempo=1, out=~ss.bus.master;
		var sig = PlayBuf.ar(2,
			bufnum:bufnum,
			rate:BufRateScale.kr(bufnum)*rate,
			startPos:BufSampleRate.kr(bufnum) * start,
			doneAction:2,
		);
		var env = Env.perc(attackTime:dur/tempo, releaseTime:releaseTime, level:amp, curve:curve);
		sig = sig * amp * EnvGen.ar(env, doneAction: 2);
		Out.ar(out, sig);
	}).add;

},

loadLibrary: {arg self, libraryName;
	var postMsgs = [], eLibrary = ();
	SoundFile.collectIntoBuffers(self.libraryPath ++ libraryName ++ "/*").do { arg buffer;
		var bufferName = buffer.path.basename.splitext[0];
		eLibrary[bufferName.asSymbol] = buffer;
		postMsgs = postMsgs.add("Loaded buffer: ~ss.buf['" ++ libraryName ++ "']['" ++ bufferName ++ "']");
	};
	self.makeModule(libraryName, eLibrary);
	~ss.postPretty(postMsgs);
},


playBuf: { arg self, libraryName, bufferName, args=[];
	var bufnum = self[libraryName.asSymbol][bufferName.asSymbol];
	~ss.synther.makeSynth("bufPlay", "bufPlay", [bufnum:bufnum]++args);
},

drone: { arg self, libraryName, bufferName, args=[];
	var bufnum = self[libraryName.asSymbol][bufferName.asSymbol];
	~ss.synther.makeSynth("bufDrone", "bufDrone", [bufnum:bufnum]++args);
},

perc: { arg self, libraryName, bufferName, args=[];
	var bufnum = self[libraryName.asSymbol][bufferName.asSymbol];
	~ss.synther.makeSynth("bufPerc", "bufPerc", [bufnum:bufnum]++args);
},



)

