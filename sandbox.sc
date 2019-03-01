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
)
~ss.midi.synthName="echoBreath";


// ---------------------------------------------------------
(
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
)

~ss.midi.synthName = "sampleShamiI";
// ---------------------------------------------------------
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
	notes:[-13, 6, 4, -9, 1],
	instrument:"sampleShamiI",
	amp:Pwhite(0.4, 0.6),
	distortion:Pwhite(0.4,0.8),
	attackTime:Pwhite(0, 0.2),
));
~ya.leafA.makeCopy("leafB", (notes:[-13, 3, -8, 3, 4]));
~ya.leafA.makeCopy("leafC", (notes:[-13, 4, -9, 4, 6]));

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

~ya.makeSeq("leafABCA",[~ya.leafA, ~ya.leafB, ~ya.leafC, ~ya.leafA]);
~ya.makeSeq("lowdist", [~ya.lowdistI, ~ya.lowdistI, ~ya.lowdistII, ~ya.lowdistI]);
~ya.makeSeq("echos", ~ya.echoBreath!4);

~ya.makeBlock("leaves", [~ya.leafABCA, ~ya.lowdist, ~ya.echos]);

)
~ya.echoBreath.playMe;

~ya.makeSeq("leaves2", ~ya.leaves!2).playMe;


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


~ss.midi.synthName="ringMe";
~ss.midi.synthName="ringEven";
~ss.midi.synthName="ringOdd";
~ss.midi.synthName="myTwinkle";
~ss.midi.synthName="echoBreath";
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

~sandbox.wonder.playMe;
~sandbox.wonderHi.playMe;
~sandbox.wonders.playMe;
~sandbox.wonder.playMe((instrument:));




