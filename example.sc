(
// RUN THIS BLOCK FIRST  (note this boots server)
// ... update the following to local path of superstudio.sc
("/Users/randallwest/code/mirrorecho/superstudio/ss.sc").loadPaths;
)



(
// RUN THIS NEXT TO LOAD COMMON MODULES + WORK-SPECIFIC STUFF
// (rerun after stopping sound with CMD-PERIOD ... )
~ss.loadCommon({
	/*
	add any additional ~ss.load, SynthDef, etc. here
	to run after common modules loaded
	EXAMPLES:
	*/

	/*
	// MIDI: simple defs for midi messaging
	~ss.load(["midi"], {
		~ss.midi.instrument = "ss.pop"; // <- sets midi instrument (default is "ss.spacey");
	});
	*/

	/*
	// BUFFERS / SOUND LIBRARY:
	~ss.buf.libraryPath = "/Users/randallwest/Echo/Sound/Library/";
	~ss.buf.loadLibrary("japan-cicadas");
	*/

});
)

~ss.buf.play("japan-cicadas", "DR0000_0192");
~ss.buf.drone("japan-cicadas", "DR0000_0192");

a = [freq:880, mul:0.5];
b = 880;
(
SynthDef("yoyo", { arg freq=220, phase=0, mul=0.5;
	var sig = SinOsc.ar(freq:freq, phase:phase, mul:mul);
	Out.ar(0, sig!2);
}).add;
)
Synth("yoyo");
t = Synth("yoyo", a);
t = Synth("yoyo", [phase:0]++a);

[phase:0]++a;

7.next.postln;

(
p = PbindProxy.new;
p.set(*[instrument:"ss.pop"]);
p.play;
)

(
Pbindef('a', *[instrument:"ss.pop"]).play;
)

(
var a;
a = Routine.new({
        3.do({ arg i; i.yield; })
    });
8.do({ a.next.postln; });    // print 4 values from stream
)
