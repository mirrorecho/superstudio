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

//////////////////////////////////////////////////////////////
// Note: This SynthDef used throughout this document
s.boot;

(
s = Server.local;
SynthDef( \help_SPE3_SimpleSine, {
    arg freq, sustain=1.0;
    var osc;
    osc = SinOsc.ar( [freq,freq+0.05.rand] ) * EnvGen.ar(
        Env.perc, doneAction: 2, levelScale: 0.3, timeScale: sustain
    );
    Out.ar(0,osc);
}).add;
)


(
SynthDef( \help_SPE3_Allpass6, { arg freq;
    var out, env;
    out = RLPF.ar(
        LFSaw.ar( freq, mul: EnvGen.kr( Env.perc, levelScale: 0.3, doneAction: 2 ) ),
        LFNoise1.kr(1, 36, 110).midicps,
        0.1
    );
    6.do({ out = AllpassN.ar(out, 0.05, [0.05.rand, 0.05.rand], 4) });
    Out.ar( 0, out );
}).add
)


(
// streams as a sequence of pitches
    var stream, dur;
    dur = 1/8;
    stream = Routine.new({
        loop({
            if (0.5.coin, {
                // run of fifths:
                24.yield;
                31.yield;
                36.yield;
                43.yield;
                48.yield;
                55.yield;
            });
            rrand(2,5).do({
                // varying arpeggio
                60.yield;
                #[63,65].choose.yield;
                67.yield;
                #[70,72,74].choose.yield;
            });
            // random high melody
            rrand(3,9).do({ #[74,75,77,79,81].choose.yield });
        });
    });
    Routine({
        loop({
            Synth(\help_SPE3_Allpass6, [ \freq, stream.next.midicps ] );
            dur.wait; // synonym for yield, used by .play to schedule next occurence
        })
    }).play
)




(
var freqStream;
freqStream = Pseq([
    Prand([
        nil,    // a nil item reached in a pattern causes it to end
        Pseq(#[24, 31, 36, 43, 48, 55]);
    ]),
    Pseq([ 60, Prand(#[63, 65]), 67, Prand(#[70, 72, 74]) ], { rrand(2, 5) }),
    Prand(#[74, 75, 77, 79, 81], { rrand(3, 9) })
], inf).asStream.midicps;

Task({
    loop({
        Synth( \help_SPE3_Allpass6, [\freq, freqStream.next ]);
        0.13.wait;
    });
}).play;
)




(
SynthDef( \help_SPE3_Mridangam, { arg t_amp;
    var out;

    out = Resonz.ar(
        WhiteNoise.ar(70) * Decay2.kr( t_amp, 0.002, 0.1 ),
        60.midicps,
        0.02,
        4
    ).distort * 0.4;

    Out.ar( 0, out );
    DetectSilence.ar( out, doneAction: 2 );
}).add;

SynthDef( \help_SPE3_Drone, {
    var out;
    out = LPF.ar(
        Saw.ar([60, 60.04].midicps)
        +
        Saw.ar([67, 67.04].midicps),
        108.midicps,
        0.007
    );
    Out.ar( 0, out );
}).add;
)




f = {arg x,y,z; [x,y,z].postln };
(
e = Environment.make{
	~x=7;
	~y=8;
	~z=9;
};
)

e.use{ f.valueEnvir(z:0) };


(
"


".speak;
)


(
// percussion solo in 10/8

var stream, pat, amp;

pat = Pseq([
    Pseq(#[0.0], 10),

    // intro
    Pseq(#[0.9, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], 2),
    Pseq(#[0.9, 0.0, 0.0, 0.2, 0.0, 0.0, 0.0, 0.2, 0.0, 0.0], 2),
    Pseq(#[0.9, 0.0, 0.0, 0.2, 0.0, 0.2, 0.0, 0.2, 0.0, 0.0], 2),
    Pseq(#[0.9, 0.0, 0.0, 0.2, 0.0, 0.0, 0.0, 0.2, 0.0, 0.2], 2),

    // solo
    Prand([
        Pseq(#[0.9, 0.0, 0.0, 0.7, 0.0, 0.2, 0.0, 0.7, 0.0, 0.0]),
        Pseq(#[0.9, 0.2, 0.0, 0.7, 0.0, 0.2, 0.0, 0.7, 0.0, 0.0]),
        Pseq(#[0.9, 0.0, 0.0, 0.7, 0.0, 0.2, 0.0, 0.7, 0.0, 0.2]),
        Pseq(#[0.9, 0.0, 0.0, 0.7, 0.2, 0.2, 0.0, 0.7, 0.0, 0.0]),
        Pseq(#[0.9, 0.0, 0.0, 0.7, 0.0, 0.2, 0.2, 0.7, 0.2, 0.0]),
        Pseq(#[0.9, 0.2, 0.2, 0.7, 0.2, 0.2, 0.2, 0.7, 0.2, 0.2]),
        Pseq(#[0.9, 0.2, 0.2, 0.7, 0.2, 0.2, 0.2, 0.7, 0.0, 0.0]),
        Pseq(#[0.9, 0.0, 0.0, 0.7, 0.2, 0.2, 0.2, 0.7, 0.0, 0.0]),
        Pseq(#[0.9, 0.0, 0.4, 0.0, 0.4, 0.0, 0.4, 0.0, 0.4, 0.0]),
        Pseq(#[0.9, 0.0, 0.0, 0.4, 0.0, 0.0, 0.4, 0.2, 0.4, 0.2]),
        Pseq(#[0.9, 0.0, 0.2, 0.7, 0.0, 0.2, 0.0, 0.7, 0.0, 0.0]),
        Pseq(#[0.9, 0.0, 0.0, 0.7, 0.0, 0.0, 0.0, 0.7, 0.0, 0.0]),
        Pseq(#[0.9, 0.7, 0.7, 0.0, 0.0, 0.2, 0.2, 0.2, 0.0, 0.0]),
        Pseq(#[0.9, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0])
    ], 30),

    // tehai : 7 beat motif 3 times sharing 1st beat with next 7x3
    // and again the third time:
    //   123456712345671234567                   123456712345671234567
    //                       123456712345671234567
    //   !                   !                   !                   !
    //   1234567890123456789012345678901234567890123456789012345678901
    Pseq(#[2.0, 0.0, 0.2, 0.5, 0.0, 0.2, 0.9,
        1.5, 0.0, 0.2, 0.5, 0.0, 0.2, 0.9,
        1.5, 0.0, 0.2, 0.5, 0.0, 0.2], 3),
    Pseq(#[5], 1),    // sam

    Pseq(#[0.0], inf)
]);

stream = pat.asStream;

Task({
    Synth(\help_SPE3_Drone);
    loop({
        if( ( amp = stream.next ) > 0,
            { Synth(\help_SPE3_Mridangam, [ \t_amp, amp ]) }
        );
        (1/8).wait;
    })
}).play
)







(
p = PbindProxy.new;
p.set(*[instrument:"ss.pop"]);
p.play;
)

(
Pbindef('a', *[instrument:"ss.pop"]).play;
)

[1,2,3,4].choose;


(
a = Routine({
	10.do({arg i; i.yield; })
});
)

b = a.squared;

b.next;

(
a = Routine.new({
	10.do({ arg i; i.yield; });
});
)

c = a.reject({ arg item; item.even });

c.next;
c.reset;

a.next;
b.next;
loop


a = Pseries(1000, 1, inf);
b = a.reject{ arg i; i.odd};
c = b.asStream;
c.next;

a  = Pseq( [1, Pseq([100,200],2), 4], 2);
b = a.asStream;
b.next;


s.boot;

(
SynthDef(\singrain, { |freq = 440, amp = 0.2, sustain = 1|
    var sig;
    sig = SinOsc.ar(freq, 0, amp) * EnvGen.kr(Env.perc(0.01, sustain), doneAction: 2);
    Out.ar(0, sig ! 2);    // sig ! 2 is the same as [sig, sig]
}).add;

)

(
r = Routine({
    var delta;
    loop {
        delta = rrand(1, 3) * 0.5;
        Synth(\singrain, [freq: exprand(200, 800), amp: rrand(0.1, 0.5), sustain: delta * 0.8]);
        delta.yield;
    }
});
)

r.play;

r.stop;



(
r = Routine({
    var delta;
    loop {
        delta = rrand(1, 3) * 0.5;
        "Will wait ".post; delta.postln;
        delta.yield;
    }
});
)
r.next;
TempoClock.default.sched(0, r);
r.stop;

r.play;
r.stop;


r.next;    // get the next value from the Routine

(
    s = Server.local;
    SynthDef(\help_SPE1, { arg i_out=0, freq;
        var out;
        out = RLPF.ar(
            LFSaw.ar( freq, mul: EnvGen.kr( Env.perc, levelScale: 0.3, doneAction: 2 )),
            LFNoise1.kr(1, 36, 110).midicps,
            0.1
        );
        // out = [out, DelayN.ar(out, 0.04, 0.04) ];
        4.do({ out = AllpassN.ar(out, 0.05, [0.05.rand, 0.05.rand], 4) });
        Out.ar( i_out, out );
    }).send(s);
)
(
// streams as a sequence of pitches
    var stream, dur;
    dur = 1/8;
    stream = Routine({
        loop({
            if (0.5.coin, {
                // run of fifths:
                24.yield;
                31.yield;
                36.yield;
                43.yield;
                48.yield;
                55.yield;
            });
            rrand(2,5).do({
                // varying arpeggio
                60.yield;
                [63,65].choose.yield;
			67.yield;
			#[70,72,74].choose.yield;
            });
            // random high melody
		rrand(3,9).do({ #[74,75,77,79,81].choose.yield });
        });
    });
    Routine({
        loop({
            Synth(\help_SPE1, [ \freq, stream.next.midicps ] );
            dur.wait; // synonym for yield, used by .play to schedule next occurence
        })
    }).play
)


)