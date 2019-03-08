(
// RUN FIRST:
// ... update the following to local path of ss.sc
("/Users/rwest/Code/mirrorecho/superstudio/ss.sc").load;
)
// ---------------------------------------------------------
(
~ss.initModule({
	~ss.buf.libraryPath = "/Users/rwest/Echo/Sounds/Library/";
	~ss.buf.loadLibrary("japan-cicadas");
	~ss.buf.loadLibrary("shamisen");
	~ss.buf.loadLibrary("stringy");
	~ss.buf.loadLibrary("piano");
	~ss.buf.loadLibrary("fluty");
	~ss.buf.loadLibrary("me-voice");
	~ss.sampler.makeDistortionSampler(
		"rainpiano",
		[
			[~ss.buf['piano']['A0'], 27.5],
			[~ss.buf['piano']['A1'], 55 ],
			[~ss.buf['piano']['A2'], 110],
			[~ss.buf['piano']['A3'], 220],
			[~ss.buf['piano']['A4'], 440],
			[~ss.buf['piano']['A5'], 880],
			[~ss.buf['piano']['A6'], 1760],
			[~ss.buf['piano']['A7'], 3520],
			[~ss.buf['piano']['A7'], 3520],
		],
	);
	~sandbox = ~ss.arrange.makeWork("sandbox");
	~sandbox.clock.tempo = 166/60;
});
)
// ---------------------------------------------------------
(
~ss.openAll;
~ss.synther.openAllLibraries;
)
// ---------------------------------------------------------

(
~ya = ~ss.makeModule("ya");
~ya.makeModule("ya1");
~ya.makeModule("ya2");
~ya.makeModule("ya3");

~ya.makeModuleList("yas", [
	~ya.ya1;
	~ya.ya2;
	~ya.ya3;
]);
)
~ya.yas.ya3.foo="BAR";
~ya.yas.ya3.foo;
~ya.ya3.foo;


(
~ss.buf.loadLibrary("echo");
~ss.synther.loadLibrary("synther.ringer");
~ss.synther.ringer;

~ss.synther.ringer.makeRinger("echoBreath", (
	bufnum:~ss.buf['echo']['nine-breath-1'],
	// overtoneAmps:[0.7, 0.5, 0.1, 0.5, 0.1, 0.4, 0.1, 0.3],
	overtoneAmps:[0.1, 0.8, 0.1, 0.7, 0.1, 0.4, 0.1, 0.3]*0.5,
	// overtoneAmps:[0.5, 0.1, 0.4, 0.05, 0.4, 0.05, 0.3, 0.05]*0.5,
	releaseTime:2.0,
	randStart:0.1,
	curve:-8,
	mix:0.4,
	rateScale:(0.25)
));

~ss.sampler.makeDistortionSampler(
	"sampleShamiI",[
		[~ss.buf['shamisen']['I-B2'], 123.5],
		[~ss.buf['shamisen']['I-C3'], 130.8],
		[~ss.buf['shamisen']['I-C#3'], 138.6],
		[~ss.buf['shamisen']['I-D3'], 146.8],
		[~ss.buf['shamisen']['I-E3'], 164.8],
		[~ss.buf['shamisen']['I-F3'], 174.6],
		[~ss.buf['shamisen']['I-F#3'], 185.0],
		[~ss.buf['shamisen']['I-G3'], 196.0],
		[~ss.buf['shamisen']['I-G#3'], 207.7],
		[~ss.buf['shamisen']['I-A3'], 220.0],
		[~ss.buf['shamisen']['I-B3'], 246.9],
		[~ss.buf['shamisen']['I-C4'], 261.6],
		[~ss.buf['shamisen']['I-C#4'], 277.2],
		[~ss.buf['shamisen']['I-E4'], 329.6],
		[~ss.buf['shamisen']['I-F4'], 349.2],
		[~ss.buf['shamisen']['I-F#4'], 370.0],
		[~ss.buf['shamisen']['I-F#4'], 370.0],
]);

~ss.sampler.makeDistortionSampler(
	"sampleShamiII",[
		[~ss.buf['shamisen']['II-F#3'], 182.0], // = 185.0 flat
		[~ss.buf['shamisen']['II-G3'], 196.0],
		[~ss.buf['shamisen']['II-G#3'], 207.7],
		[~ss.buf['shamisen']['II-A3'], 220.0],
		[~ss.buf['shamisen']['II-B3'], 247.9],
		[~ss.buf['shamisen']['II-C#4'], 277.2],
		[~ss.buf['shamisen']['II-D4'], 293.7],
		[~ss.buf['shamisen']['II-E4'], 329.6],
		[~ss.buf['shamisen']['II-F#4'], 369.0],
		[~ss.buf['shamisen']['II-G4'], 392.0],
		[~ss.buf['shamisen']['II-G#4'], 420.0], // = 415.3 sharp
		[~ss.buf['shamisen']['II-B4'], 500.8], // = 493.8 flat
		[~ss.buf['shamisen']['II-C#5'], 554.4],

]);
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
)
// ---------------------------------------------------------
~ss.midi.synthName = "echoBreath";
~ss.midi.synthName = "meYo";
~ss.midi.synthName = "rainpiano";
~ss.midi.synthName = "sampleShamiI";
~ss.midi.synthName = "sampleShamiII";
~ss.midi.synthName = "ssSawBass";
~ss.midi.postNote = true;
// ---------------------------------------------------------
[1,2,3,4]*[10,100,1000,10000];

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

)
1.124**2;

(

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
	rhythm:0.5!32,
	distortion:Pwhite(0.3, 0.6),
	attackTime:Pwhite(0.1, 0.3),
	releaseTime:Pwhite(0.6, 2),
	curve:Pwhite(8,-8),
));
~rainVI.pianoCycle1.makeCopy("pianoCycle2", (
	rhythm:0.5!16,
));
~rainVI.pianoCycle1.makeCopy("pianoCycleII2", (
	note:Pseq([1, 2, -3], 4) + Prand([0,12,24], inf),
	rhythm:0.5!16,
));
~rainVI.pianoCycle1.makeCopy("pianoCycle3", (
	note:Pseq([1, -6, -3], 4) + Prand([0,12,24], inf),
	rhythm:0.5!16,
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
	rhythm:[16],
	amp:0.6,
));
~rainVI.bass1.makeCopy("bass2", (note:-18, rhythm:[8]));
~rainVI.bass1.makeCopy("bass3", (note:-25, rhythm:[8]));
~rainVI.bass1.makeCopy("bass4", (note:-28));
~rainVI.makeSeq("bassCycle", [
	~rainVI.bass1,
	~rainVI.bass2,
	~rainVI.bass3,
	~rainVI.bass4,
]);

~rainVI.makeP("melodyI", (
	instrument:"meYo",
	rhythm:[6, 1.5, 1.5, 0.5, 2.5, 36],
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

(
r = ~rainVI.makeSeq("rr", [
	~rainVI.pianoCycle1,
	~rainVI.pianoCycleII2,
	~rainVI.pianoCycle3,
	~rainVI.pianoCycle4,
]);

)
r.listSize;


~rainVI.pianoCycleII.list;


~rainVI.cycles.playMe;
~rainVI.melodyI.playMe;

~rainVI.cycleBlockI.playMe;

~rainVI.pianoCycleI.playMe;
~rainVI.pianoCycle1.playMe;


(
~ya = ~ss.arrange.makeWork("ya");
~ya.clock.tempo = 80/60;

~ya.makeP("lowdistI", (
	instrument:"rainpiano",
	notes:[\rest, -25],
	rhythm:[1, 4],
	amp:Pwhite(0.3, 0.4),
	distortion:Pwhite(0.8, 0.9),
	// curve:-4;
));
~ya.lowdistI.makeCopy("lowdistII", (notes:[\rest, -27]));

~ya.makeP("leafA", (
	rhythm:[0.5, 1, 0.5, 2, 1],
	notes:[-13, 6, 4, -9, \rest],
	instrument:"sampleShamiI",
	amp:Pwhite(0.4, 0.6),
	distortion:Pwhite(0.4,0.8),
	attackTime:Pwhite(0, 0.2),
));
~ya.leafA.makeCopy("leafB", (notes:[-13, 3, -8, 3, 4]));
~ya.leafA.makeCopy("leafC", (notes:[-13, 4, \rest, 4, \rest]));
~ya.leafA.makeCopy("leafA2", (notes:[-13, 6, 4, -9, 6]));

~ya.makeP("echoBreathHi", (
	bufnum:~ss.buf['echo']['nine-breath-1'],
	instrument:"echoBreath",
	note:Prand([-3, 4, 11, 21, 18, 25, 32, 39], inf),
	amp:Pwhite(0.02, 0.1),
	rhythm:0.25!20,
	attackTime:Pwhite(0.01, 0.2),
	releaseTime:Pwhite(0.5, 1),
	randStart:0.1,
	mix:0.2,
	curve:-8,
	rateScale:0.5,
));
~ya.echoBreathHi.makeCopy("echoBreathLo", (note:Prand([-3, 4, 11, 21, 18, 25, 32, 39]-12, inf)));
~ya.makeBlock("echoBreath", [~ya.echoBreathHi, ~ya.echoBreathLo]);

~ya.makeSeq("leafABCA",[~ya.leafA, ~ya.leafB, ~ya.leafC, ~ya.leafA2]);
~ya.makeSeq("lowdist", [~ya.lowdistI, ~ya.lowdistI, ~ya.lowdistII, ~ya.lowdistI]);
~ya.makeSeq("echos", ~ya.echoBreath!4);

~ya.makeBlock("leaves", [~ya.leafABCA, ~ya.lowdist, ~ya.echos]);

~ya.makeP("hiPiano", (
	instrument:"rainpiano",
	notes:[27, 28, 35, 27, 28, 21],
	amp:Pwhite(0.5, 0.6),
	dur:Pshuf([0.5, 0.5, 1, 1, 2])
));


)
~ya.echoBreath.playMe;
~ya.leafABCA.playMe;
~ya.hiPiano.playMe;

~ya.makeSeq("leaves2", ~ya.leaves!4).playMe;
~ya.leafA.makeCopy("leafAShuf", (note:Pshuf(~ya.leafA.notes))).playMe;
~ya.makeSeq("_", ~ya.leafAShuf!8).playMe;


(
~sandbox.makeP("offdrop1", (
	instrument:"rainpiano",
	notes:[0,-5,-5,0,-7,-7,-2,-5],
	rhythm:[0.5,1,1,1,1,1,1,1.5],
	amp:Pseq([\rest, 0.4, 0.37, 0.35, 0.33, 0.3, 0.25, 0.2 ]) * Pwhite(1,1.1),
	distortion:Pseq([0.05,0.1,0.2,0.25,0.3,0.35,0.4,0.45]*2) * Pwhite(1.5,2),
	attackTime:Pwhite(0.02,0.06),
	releaseTime:Pwhite(2,4),
	curve:-4;
));
~sandbox.makeP("offdrop2", (
	instrument:"rainpiano",
	note:Pseq([-29],inf),
	rhythm:4!2,
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
~ringer.makeRinger("myTwinkle", (
	bufnum:~ss.buf['japan-cicadas']['0185-insects-water-kyoto'],
	overtoneAmps:[0.1, 0.8, 0.1, 0.7, 0.1, 0.4, 0.1, 0.3],
	releaseTime:4.0,
	randStart:4.0,
	curve:-22,
	mix:0.6,
	rateScale:(1/2)
));

~ringer.makeRinger("echoBreath", (
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
	rhythm:1!96,
	amp:Pstutter(7, Pwhite(0.1, 0.2)),
	releaseTime:Pwhite(0.2, 0.9),
	rq:0.5,
));

~factor.makeP("f2", (
	instrument:"rainpiano",
	note:Pseq([\rest, 0], inf),
	rhythm:1!96,
	amp:Pstutter(2*7, Pwhite(0.2, 0.36)),
	releaseTime:Pwhite(0.3, 0.6),
	distortion:Pstutter(2*3, Pwhite(0.2, 1)),
));

~factor.makeP("f3", (
	instrument:"rainpiano",
	note:Pseq([\rest, \rest, 12], inf),
	rhythm:1!96,
	amp:Pstutter(3*7, Pwhite(0.2, 0.4)),
	decayTime:1,
));

~factor.makeP("f4", (
	instrument:"rainpiano",
	note:Pseq([\rest, \rest, \rest, -24], inf),
	rhythm:1!96,
	distortion:1,
	amp:Pstutter(4*7, Pwhite(0.2, 0.6)),
	decayTime:1,
	curve:-22,
));

~factor.makeP("f5", (
	instrument:"sampleShamiI",
	note:Pseq([\rest, \rest, \rest, \rest, 7], inf),
	rhythm:1!96,
	amp:Pstutter(5*7, Pwhite(0.3, 0.6)),
	decayTime:1,
));

~factor.makeP("f6", (
	instrument:"rainpiano",
	note:Pseq([\rest, \rest, \rest, \rest, \rest, 19], inf),
	rhythm:1!96,
	amp:Pstutter(6*7, Pwhite(0.1, 0.3)),
	decayTime:1,
));

~factor.makeP("f7", (
	instrument:"sampleShamiI",
	note:Pseq([\rest, \rest, \rest, \rest, \rest, \rest, -5], inf),
	rhythm:1!96,
	amp:0.6,
	decayTime:1,
));

// ~factor.makeP("f8", (
// 	instrument:"sampleShamiI",
// 	note:Pseq([\rest, \rest, \rest, \rest, \rest, \rest, \rest, -12], inf),
// 	rhythm:1!96,
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
	rhythm:0.5!32,
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
	notes:[[0,2,5,7]],
	rhythm:[16],
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




