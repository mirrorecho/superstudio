(
// RUN FIRST:
// ... update the following to local path of ss.sc
("/Users/rwest/Code/mirrorecho/superstudio/ss.sc").load;
)
/ ---------------------------------------------------------
~ss.openAll;
/ ---------------------------------------------------------
(
~ss.initModule({
	~ss.buf.libraryPath = "/Users/rwest/Echo/Sounds/Library/";
	~ss.buf.loadLibrary("japan-cicadas");
	~ss.buf.loadLibrary("shamisen");
	~ss.buf.loadLibrary("stringy");
	~ss.buf.loadLibrary("piano");
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
		],
	);
	~sandbox = ~ss.arrange.makeWork("sandbox");
	~sandbox.clock.tempo = 166/60;
});
)
/ ---------------------------------------------------------
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

// ---------------------------------------------------------

~sandbox.off.playMe;

MIDIClient.init;
MIDIClient.list;

~ss.buf['piano']['A3'].play;
~ss.buf.perc('stringy','G3-sulpont-surge', [start:5.464, out:~ss.bus.rainEcho, attackTime:0.2]);
~ss.buf.perc('stringy','G3-sulpont-surge', [start:3, releaseTime:2, out:~ss.bus.rainEcho, attackTime:0.2]);

~ss.midi.synthName="rainSpacey";