(
// RUN FIRST:
("".resolveRelative ++ "../superstudio/ss.sc").load;
)

// to explore:
// - combo change pitch between mix and sliding ( split betwen varios harmonics)


// ---------------------------------------------------------
(
~ss.initModule({
	// TO DO: make a settings NOT in source control for this
	~ss.buf.libraryPath = "/Users/rwest/Echo/Sounds/Library/";
	~ss.buf.loadLibrary("japan-cicadas");
	~ss.buf.loadLibrary("shamisen");
	~ss.buf.loadLibrary("stringy");
	~ss.buf.loadLibrary("piano");
	~ss.buf.loadLibrary("fluty");
	~ss.buf.loadLibrary("echo");
	~ss.buf.loadLibrary("noisy");
	~ss.buf.loadLibrary("me-voice");

	~ss.synther.loadLibrary("synther.ringer");
	~sandbox = ~ss.arrange.makeWork("sandbox");
	~sandbox.clock.tempo = 166/60;

	~ss.midi.synthName = "sandboxSampler";

});
)
// ---------------------------------------------------------

~yo.name;

~ss.sampler.makeSampler("sandboxSampler", "play", "pianoI");

~ss.sampler.makeSampler("sandboxSampler", "drone", "pianoI", (ampScale:6) );
~ss.sampler.makeSampler("sandboxSampler", "droneDistortion", "pianoI", (ampScale:6, distortion:0.9) );
~ss.sampler.makeSampler("sandboxSampler", "droneStereoFloat", "fluteI" );

~ss.sampler.makeSampler("sandboxSampler", "distortionPerc", "pianoI", (distortion:0.9, releaseTime:0.5) );
~ss.sampler.makeSampler("sandboxSampler", "distortionAdsr", "pianoI", (distortion:0.9) );
~ss.sampler.makeSampler("sandboxSampler", "stereoFloat", "fluteI", (panFreqRange:1.1) );
~ss.sampler.makeSampler("sandboxSampler", "stereoFloatAdsr", "fluteI", (releaseTime:1) );
~ss.sampler.makeSampler("sandboxSampler", "stereoFloatPerc", "fluteI", (releaseTime:0.5) );



~ss.sampler.makeSampler("sampledMeYo", "basic", "meYo");
~ss.sampler.makeSampler("sampledPiano", "basic", "pianoI");
~ss.sampler.makeSampler("sampledPianoAdsr", "playAdsr", "pianoI");
~ss.sampler.makeSampler("sampledPianoPerc", "perc", "pianoI");

~ss.sampler.makeSampler("sampledShamiI", "basic", "shamiI");
~ss.sampler.makeSampler("sampledShamiII", "basic", "shamiII");

~ss.sampler.makeSampler("sampledFlute", "basic", "fluteI", (channels:1));

~ss.sampler.makeSampler("sampledPianoDistorted", "protoEnv", "pianoI");


a = ~ss.sampler.makers.loadModule("protoEnv");
a;

(
~ss.openAll;
~ss.synther.openAllLibraries;
)

~ss.sampler.sampleData.openAll;
~ss.sampler.makers.openAll;

~ss.synther.makeSynth("yo", "sampledPianoDistortion", (distortion:0.1, releaseTime:0.2).asPairs);



// ---------------------------------------------------------
~ss.midi.synthName = "ghostyMoan";

~ss.midi.synthName = "echoBreath";
~ss.midi.synthName = "echoBreathing";
~ss.midi.synthName = "meYo";
~ss.midi.synthName = "sandboxSampler";

~ss.midi.synthName = "sampledMeYo";
~ss.midi.synthName = "sampledPiano";
~ss.midi.synthName = "sampledPianoAdsr";
~ss.midi.synthName = "sampledPianoPerc";
~ss.midi.synthName = "sampledPianoDistortion";
~ss.midi.synthName = "sampledShamiI";
~ss.midi.synthName = "sampledShamiII";

~ss.midi.synthName = "sampledFlute";

~ss.midi.synthName = "ssSawBass";
~ss.midi.synthName = "rainStringThin";
~ss.midi.synthName = "rainFluteFlutter";
~ss.midi.synthName = "rainFluteHum";
~ss.midi.synthName = "room2";
~ss.midi.postNote = true;
// ---------------------------------------------------------

~ss.synther.makeSynth("echoBreath");
~ss.buf['echo']['nine-breath-1'].play;

// ---------------------------------------------------------

/*
MACHINES LIVE PERFORMANCE IDEAS:

What controls input?
- Phone app!!!! (this is most interesting)
- MIDI keyboard?
- MICI controller?
- mics?
- fancy contraption / sensor

How can input be expressive?
- pure theater?
- shaking phone

How to integrate performance?

Title
- "Japan in my pocket"
- "The noodle is cooked"
- "Where are my toes?!"
- "I was raised by a happy fungus, sometime in July"
- "Fear of gastronomy"


*/

~ss.bus.makeControlBus("accelX");

~ss.osc.addBusListener("accelX1", (path:"/accelerometer/raw/x", busName:"accelX", msgSelect:1));

(
~ss.osc.addFuncListener("accelX2", (path:"/accelerometer/raw/x", msgSelect:1, func:{
	arg msg;
	var freq = msg[1].linexp(-20,20,220,880);
	freq.postln;
	~ss.synther.ghostyMoan.set(\freq, freq);
}));
)


~ss.synther.loadLibrary("synther.ghosty");
~ss.synther.makeSynth("ghostyMoan");
~ss.synther.ghostyMoan.free;

Synth

~ss.osc.removeListener("accelX");
~ss.osc.removeListener("accelX2");

~ss.bus.accelX;

~ss.bus.accelX.getSynchronous;

NamedControl

(
~ghosty = ~ss.synther.makeModule("fish", (

	eMoanDefaultArgs: (
		oscType:SinOsc,
		freqMul:1 + (1/8),
		moanRate:4,
		panRate:3,
		panMul:0.8
	),

	makeMoan: { arg self, name, eArgs=();
		var myArgs = self.eMoanDefaultArgs ++ eArgs;

		SynthDef(name, { arg out=~ss.bus.master, amp=0.2, gate=1, freq=440;
			var sig, env;
			var freqMul = \freqMul.kr(myArgs.freqMul, 0.5);
			var lf=LFNoise1.kr(freq:myArgs.moanRate);
			var moanFreq = freq*(freqMul**lf);

			sig = Pan2.ar(
				in:myArgs.oscType.ar(freq:moanFreq, mul:amp*0.6),
				pos:LFNoise2.kr(myArgs.panRate, mul:myArgs.panMul),
			);

			env = EnvGen.kr(Env.adsr(attackTime:0.1, sustainLevel:1.0, releaseTime:0.5), gate:gate, doneAction:2);

			Out.ar(out, sig*env);

		}).add;
	},
));

// ~ghosty.makeMoan("ghostyMoan");
// ~ghosty.makeMoan("ghostyBigMoan", (oscType:Saw, freqMul:2, moanRate:8, panRate:1, panMul:0.6));

)
~ghosty.makeMoan("ghostyMoan");
// ~ghosty.makeMoan("ghostyBigMoan", (oscType:Saw, freqMul:2, moanRate:8, panRate:1, panMul:0.6));
~ghosty.makeMoan("ghostyMoan");

Synth

(
~ss.synther.ringer.makeRinger("echoBreath", (
	bufnum:~ss.buf['echo']['nine-breath-1'],
	//overtoneAmps:[0.7, 0.5, 0.1, 0.5, 0.1, 0.4, 0.1, 0.3],
	// overtoneAmps:[0.1, 0.8, 0.1, 0.7, 0.1, 0.4, 0.1, 0.3]*0.5,
	overtoneAmps:[0.5, 0.1, 0.4, 0.05, 0.4, 0.05, 0.3, 0.05]*0.5,
	releaseTime:2.0,
	randStart:0.2,
	curve:-12,
	mix:1,
	rateScale:(0.25)
));
~ss.synther.ringer.makeDroneRinger("echoBreathing", (
	bufnum:~ss.buf['echo']['nine-breath-1'],
	// overtoneAmps:[0.7, 0.5, 0.1, 0.5, 0.1, 0.4, 0.1, 0.3],
	// overtoneAmps:[0.1, 0.8, 0.1, 0.7, 0.1, 0.4, 0.1, 0.3]*0.5,
	overtoneAmps:[0.5, 0.1, 0.4, 0.05, 0.4, 0.05, 0.3, 0.05]*0.5,
	// releaseTime:2.0,
	// curve:-12,
	mix:0.4,
	rateScale:(0.25)
));

~ss.synther.ringer.makeDroneRinger("room2", (
	bufnum:~ss.buf['noisy']['windy-day-bathroom-2'],
	// overtoneAmps:[0.7, 0.5, 0.1, 0.5, 0.1, 0.4, 0.1, 0.3],
	// overtoneAmps:[0.1, 0.8, 0.1, 0.7, 0.1, 0.4, 0.1, 0.3]*0.5,
	overtoneAmps:[0.5, 0.1, 0.4, 0.05, 0.4, 0.05, 0.3, 0.05]*0.5,
	// releaseTime:2.0,
	// curve:-12,
	attackTime:0.2,
	mix:0.8,
	rateScale:(0.5)
));




~ss.sampler.makeDroneSampler(
	"rainStringThin",
	[
		[~ss.buf['stringy']['D#3-mod-thin-vibrato'], 311.127],
		[~ss.buf['stringy']['D#3-mod-thin-vibrato'], 311.127],
	],
);
~ss.sampler.makeDroneStereoFloatSampler(
	"rainFluteFlutter",
	[
		[~ss.buf['fluty']['B4-flutter'], 493.883],
		[~ss.buf['fluty']['B4-flutter'], 493.883],
	],
);
~ss.sampler.makeDroneStereoFloatSampler(
	"rainFluteHum",
	[
		[~ss.buf['fluty']['E4-B4-hum'], 329.628],
		[~ss.buf['fluty']['E4-B4-hum'], 329.628],
	],
);

)

// ---------------------------------------------------------

~ss.buf.drone("stringy", "D#3-mod-thin-vibrato");

a = 3.979;
b = 3.collect{ |i| (i+1)*a/8; };
Env(levels: [0.5, 0.25, 0, 0.25, 0.5], times: b, curve: [6,-6,6,-6]).circle.plot;




(c   "/Users/rwest/Code/mirrorecho/superstudio/ss.sc").load;

(
~ss.makeModule("aTest");
~ss.makeModule("bTest");

~ss.makeModuleList("test", [
	~ss.aTest,
	~ss.bTest,
]);

~ss.test.getCopy;
)

~ss.test.getCopy;


~ss.test.postMe;

(0.9**4)!4;

e = ();
e["foo"];

() ++ e;
() ++ e["foo"];

((a:3) ? ())

(a:1) | nil;

e = ~ss.buf['japan-cicadas'].postMe;


Set(a:4) | Set(a:4, b:6);


(

// ---------------------------------------------------------

(

SynthDef("ssSawBass", {arg amp=0.6, attackTime=0.04, decayTime=0.4, releaseTime=1.0,
	freqSpread=1.002, freq=100, gate=1, out=~ss.bus.master;
	var sig, env;
	var freqSpreadSquared = freqSpread**2;

	sig = [
		Saw.ar([
			freq * freqSpreadSquared,
			freq / freqSpread,
			freq * freqSpread,
			freq / freqSpreadSquared,
		])
	];
	sig = sig * (3 + LFNoise1.ar(16!4));

	sig = Splay.ar(sig, Rand(0.44,0.88), spread:0.69) * AmpCompA.kr(freq,20);

	sig = sig / 3;

	sig = RLPF.ar(sig, freq*4, rq:1);

	// sig = sig**4;

	env = EnvGen.kr( Env.adsr(
		attackTime:attackTime,
		decayTime:decayTime,
		sustainLevel:0.66,
		peakLevel:amp**4,
		releaseTime:releaseTime,
		curve:-2,
	), gate:gate, doneAction:2);

	sig = sig * env;
	Out.ar(out, sig);

}).add;



SynthDef("ssBass", {arg amp=1, t_trig=1, freq=100, rq=0.004, gate=1, out=~ss.bus.master;
	var signal, signal1, signal2, b1, b2;
	b1 =  [0, 0.01, 0.02, 0.04] + 1.92; // 1.9522665452781; // = 1.98 * 0.989999999 * cos(0.09);
	b2 =  [0, 0.002, 0.004, 0.009] + 0.99 * -1; // -0.998057;
	// t_trig.scope;
	signal = K2A.ar(t_trig);
	// signal.scope;
	signal = SOS.ar(signal, 0.09, 0.0, 0.0, b1, b2);
	signal = RHPF.ar(signal, freq, rq) + RHPF.ar(signal, freq/2, rq);
	signal = Splay.ar(signal, 0.66);
	// signal = Decay2.ar(signal, 0.4, 0.3) * signal;
	signal = (signal**3) * (amp**4);
	signal = signal * EnvGen.kr( Env.adsr(
		attackTime:0.3, decayTime:0.3, curve:-4), gate:gate, doneAction:2) * amp;

	Out.ar(out, signal);
}).add;

~rainVI = ~ss.arrange.makeWork("rainVI");
~rainVI.clock.tempo = 144 / 60;
~rainVI.makeP("pianoCycle1", (
	instrument:"rainpiano",
	note:Pseq([1, -1, -3], 4) + Prand([0,12,24], inf),
	amp:Pwhite(0.3,0.4),
	dur:Pseq(0.5!32),
	distortion:Pwhite(0.3, 0.6),
	attackTime:Pwhite(0.1, 0.3),
	releaseTime:Pwhite(0.6, 2),
	curve:Pwhite(8,-8),
));
~rainVI.pianoCycle1.makeCopy("pianoCycle2", (
	dur:Pseq(0.5!16),
));
~rainVI.pianoCycle1.makeCopy("pianoCycleII2", (
	note:Pseq([1, 2, -3], 4) + Prand([0,12,24], inf),
	dur:Pseq(0.5!16),
));
~rainVI.pianoCycle1.makeCopy("pianoCycle3", (
	note:Pseq([1, -6, -3], 4) + Prand([0,12,24], inf),
	dur:Pseq(0.5!16),
));
~rainVI.pianoCycle1.makeCopy("pianoCycle4", (
	note:Pseq([1, -6, -1], 4) + Prand([0,12,24], inf),
));
~rainVI.makeSeq("pianoCycleI", [
	~rainVI.pianoCycle1,
	~rainVI.pianoCycle2,
	~rainVI.pianoCycle3,
	~rainVI.pianoCycle4,
]);
~rainVI.makeSeq("pianoCycleII", [
	~rainVI.pianoCycle1,
	~rainVI.pianoCycleII2,
	~rainVI.pianoCycle3,
	~rainVI.pianoCycle4,
]);

~rainVI.makeP("bass1", (
	instrument:"ssSawBass",
	note:-20,
	dur:Pseq([16]),
	amp:0.6,
));
~rainVI.bass1.makeCopy("bass2", (note:-18, dur:Pseq([8]) ));
~rainVI.bass1.makeCopy("bass3", (note:-25, dur:Pseq([8]) ));
~rainVI.bass1.makeCopy("bass4", (note:-28));
~rainVI.makeSeq("bassCycle", [
	~rainVI.bass1,
	~rainVI.bass2,
	~rainVI.bass3,
	~rainVI.bass4,
]);

~rainVI.makeP("melodyI", (
	instrument:"meYo",
	dur:Pseq([6, 1.5, 1.5, 0.5, 2.5, 36]),
	note:Pseq([\rest, -3, -3, -3, -1, \rest], inf),
	amp:0.6,
));

~rainVI.makeBlock("cycleBlockI", [
	~rainVI.bassCycle,
	~rainVI.pianoCycleI,
	~rainVI.melodyI,
]);
~rainVI.makeBlock("cycleBlockII", [
	~rainVI.bassCycle,
	~rainVI.pianoCycleI,
]);


~rainVI.makeSeq("cycles", [
	~rainVI.cycleBlockI,
	~rainVI.cycleBlockII,
]);




)

~rainVI.pianoCycleI.list;


~rainVI.cycles.playMe;
~rainVI.melodyI.playMe;

~rainVI.cycleBlockI.playMe;

~rainVI.pianoCycleI.playMe;
~rainVI.pianoCycle1.playMe;



(
~sandbox.makeP("offdrop1", (
	instrument:"rainpiano",
	note:Pseq([0,-5,-5,0,-7,-7,-2,-5]),
	dur:Pseq([0.5,1,1,1,1,1,1,1.5]),
	amp:Pseq([\rest, 0.4, 0.37, 0.35, 0.33, 0.3, 0.25, 0.2 ]) * Pwhite(1,1.1),
	distortion:Pseq([0.05,0.1,0.2,0.25,0.3,0.35,0.4,0.45]*2) * Pwhite(1.5,2),
	attackTime:Pwhite(0.02,0.06),
	releaseTime:Pwhite(2,4),
	curve:-4;
));
~sandbox.makeP("offdrop2", (
	instrument:"rainpiano",
	note:Pseq([-29],inf),
	dur:Pseq(4!2),
	amp:Pseq([0.5,0.3],inf),
	distortion:Pseq([1,0],inf),
	curve:4,
));
~sandbox.makeBlock("off",
	[
		~sandbox['offdrop1'],
		~sandbox['offdrop2'],
	]
);
)


~ss.buf.loadLibrary("echo");

~ss.synther.loadLibrary("synther.ringMe");

a = ();
~ss.postln;

// ---------------------------------------------------------
(
~ss.synther.ringer.makeRinger("myTwinkle", (
	bufnum:~ss.buf['japan-cicadas']['0185-insects-water-kyoto'],
	overtoneAmps:[0.1, 0.8, 0.1, 0.7, 0.1, 0.4, 0.1, 0.3],
	releaseTime:4.0,
	randStart:4.0,
	curve:-22,
	mix:0.6,
	rateScale:(1/2)
));

~ss.synther.ringer.makeRinger("echoBreath", (
	bufnum:~ss.buf['echo']['nine-breath-1'],
	overtoneAmps:[0.7, 0.5, 0.1, 0.5, 0.1, 0.4, 0.1, 0.3],
	releaseTime:2.0,
	randStart:0.1,
	curve:-12,
	mix:0.6,
	rate:2,
	rateScale:(1/3)
));
)

// ---------------------------------------------------------
~ss.midi.synthName="ringMe";
~ss.midi.synthName="ringEven";
~ss.midi.synthName="ringOdd";
~ss.midi.synthName="myTwinkle";
~ss.midi.synthName="echoBreath";
~ss.midi.synthName="sampleShamiI";
~ss.midi.synthName="noiseHitI";
~ss.midi.synthName="rainpiano";
~ss.midi.postNote = true;
// ---------------------------------------------------------

~sandbox.off.playMe;

MIDIClient.init;
MIDIClient.list;

~ss.buf['piano']['A3'].play;
~ss.buf.perc('stringy','G3-sulpont-surge', [start:5.464, out:~ss.bus.rainEcho, attackTime:0.2]);
~ss.buf.perc('stringy','G3-sulpont-surge', [start:3, releaseTime:2, out:~ss.bus.rainEcho, attackTime:0.2]);

~ss.buf['stringy']['D#3-mod-thin-vibrato'].play;
~ss.buf['stringy']['E3-violin-nice-decresc'].play;
~ss.buf['stringy']['A2-violin-basic-vibrato'].play;


() / 480;
90*8;
7.factorial / 90;
2*3*5*7*11*13 / 640;

0.4!(2*3);
1/0.667;


(

~ss.synther.loadLibrary("synther.noiseHits");


~factor = ~ss.arrange.makeWork("factor");

~factor.times = 7.factorial;

~factor.clock.tempo = 640 / 60;

~factor.makeP("f1", (
	instrument:Prand(["noiseHitI", "rainpiano"], inf),
	note:Prand([12,19,24], inf),
	dur:Pseq(1!96),
	amp:Pstutter(7, Pwhite(0.1, 0.2)),
	releaseTime:Pwhite(0.2, 0.9),
	rq:0.5,
));

~factor.makeP("f2", (
	instrument:"rainpiano",
	note:Pseq([\rest, 0], inf),
	dur:Pseq(1!96),
	amp:Pstutter(2*7, Pwhite(0.2, 0.36)),
	releaseTime:Pwhite(0.3, 0.6),
	distortion:Pstutter(2*3, Pwhite(0.2, 1)),
));

~factor.makeP("f3", (
	instrument:"rainpiano",
	note:Pseq([\rest, \rest, 12], inf),
	dur:Pseq(1!96),
	amp:Pstutter(3*7, Pwhite(0.2, 0.4)),
	decayTime:1,
));

~factor.makeP("f4", (
	instrument:"rainpiano",
	note:Pseq([\rest, \rest, \rest, -24], inf),
	dur:Pseq(1!96),
	distortion:1,
	amp:Pstutter(4*7, Pwhite(0.2, 0.6)),
	decayTime:1,
	curve:-22,
));

~factor.makeP("f5", (
	instrument:"sampleShamiI",
	note:Pseq([\rest, \rest, \rest, \rest, 7], inf),
	dur:Pseq(1!96),
	amp:Pstutter(5*7, Pwhite(0.3, 0.6)),
	decayTime:1,
));

~factor.makeP("f6", (
	instrument:"rainpiano",
	note:Pseq([\rest, \rest, \rest, \rest, \rest, 19], inf),
	dur:Pseq(1!96),
	amp:Pstutter(6*7, Pwhite(0.1, 0.3)),
	decayTime:1,
));

~factor.makeP("f7", (
	instrument:"sampleShamiI",
	note:Pseq([\rest, \rest, \rest, \rest, \rest, \rest, -5], inf),
	dur:Pseq(1!96),
	amp:0.6,
	decayTime:1,
));

// ~factor.makeP("f8", (
// 	instrument:"sampleShamiI",
// 	note:Pseq([\rest, \rest, \rest, \rest, \rest, \rest, \rest, -12], inf),
// 	dur:Pseq(1!96),
// 	distortion:1,
// 	amp:0.8,
// 	decayTime:1,
// ));

~factor.makeBlock("factors", [
	~factor.f1,
	~factor.f2,
	~factor.f3,
	~factor.f4,
	~factor.f5,
	~factor.f6,
	~factor.f7,
	// ~factor.f8,
]
);

)
~factor.factors.playMe;

~factor.f2.playMe;




(
~ss.sampler.makeDistortionSampler(
	"meYo",
	[
		[~ss.buf['me-voice']['yo-E3'], 164.8],
		[~ss.buf['me-voice']['yo-E3'], 164.8],
		[~ss.buf['me-voice']['yo-Ab3'], 207.7],
		[~ss.buf['me-voice']['yo-C4'], 261.6],
		[~ss.buf['me-voice']['yo-C4'], 261.6],
	],
);

~ss.sampler.makeStereoFloatSampler(
	"flutys",
	[
		[~ss.buf['fluty']['E4'], 329.6],
		[~ss.buf['fluty']['E4'], 329.6],
	],
);
)

~ss.midi.synthName="meYo";





Synth("meYo", [freq:65.41, amp:0.8, distortion:1]);
~ss.bus.master;
~sandbox;
(
~sandbox.makeP("wonder", (
	instrument:"sampleShamiI",
	// note:Prand([[-8,0],[-7,-5],-5,-3,-2,[0,2], \rest, \rest, \rest], inf),
	note:Prand([-13,-10,-8, \rest], inf),
	dur:Pseq(0.5!32),
	amp:Pwhite(0.4,0.5),
	distortion:Pwhite(0, 0.4),
	attackTime:Pwhite(0, 0.2),
	releaseTime:Pwhite(1.0,2.0),
	// out:~ss.bus.master,
)).playMe;
)
(
~sandbox.makeP("wonderHi", (
	instrument:"flutys",
	note:[0,2,5,7],
	dur:Pseq([16]),
	// amp:Pwhite(0.4,0.5),
	// distortion:Pwhite(0, 0.9),
	// out:~ss.bus.master,
));
)
(
~sandbox.makeBlock("wonders",
	[
		~sandbox.wonder,
		~sandbox.wonderHi,
	]
);
)

p = Pser([1,2,3,4,5]);
p.list;

e = (play:{"YOYO".postln;},);
f = ().putAll(e);
g = f.putAll((yo:"MAMA"))

e.play;
f.play;
g.play;
g.parentEvents;

g.next;

~ss.play;
Event



~sandbox.wonder.playMe;
~sandbox.wonderHi.playMe;
~sandbox.wonders.playMe;
~sandbox.wonder.playMe((instrument:));




