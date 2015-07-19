(

~printNice = { arg msgs=[""];
    "-----------------------".postln;
    msgs.do {arg msg; msg.postln; };
    " ".postln;
};

{ 
    /* 
    a Function for defining then synth defs, so that its receivier is a Routine, which can wait for
    for the defs to load before calling them...
    */
    ~masterBus = Bus.audio(s,2);
    ~masterOutBus = Bus.audio(s,2); // master out (without routing through master fx)

    ~printNice.value(["~masterBus created... send all synth outputs here",
        "do not pass go do not collect $200"]);

    SynthDef("masterFx", { arg reverbRoom=0.44, reverbMix=0.2;
        var sig = In.ar(~masterBus,2);
        sig = FreeVerb2.ar(sig[0], sig[1], room:reverbRoom, mix:reverbMix);
        Out.ar(~masterOutBus, sig);
    }).add;

    SynthDef("masterOut", {
        var sig = In.ar(~masterOutBus,2);
        sig = Limiter.ar(sig, 0.9);
        Out.ar(0, sig);
    }).add;
    
    s.sync;
    
    ~masterOut = Synth("masterOut");
    s.sync;
    ~masterFx = Synth("masterFx");
    ~printNice.value(["~masterFx and ~masterOut synths created"]);


}.fork; 

)

