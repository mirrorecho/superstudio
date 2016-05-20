(

~ss.makeModule(
    "synth.library", // module name
    ["synth"], // namespace hierarchy for module
    "Simple Synth Library", // friendly name
    { arg ss, module; // module function...

        SynthDef("ss.spacey", { 
            arg freq=440, amp=0.1, gate=1;
            var sig1, sig2, env1, env2;
            sig1 = SinOsc.ar(freq:freq, mul:amp/2);
            sig1 = sig1 + SinOsc.ar(freq:freq*2, mul:amp/3);
            env1 = EnvGen.kr(Env.adsr(0.02, 0.1, 0.4, 0.9), gate:gate);
            sig1 = sig1 * env1;


            sig2 = Pulse.ar(freq:freq, mul:amp/6);
            sig2 = RLPF.ar(sig2, LFNoise1.kr(8!2).range(freq*2, 9900), 0.2);
            env2 = EnvGen.kr(Env.adsr(0.8, 0.2, 0.4, 3), gate:gate, doneAction:2);
            

            sig2 = sig2 * env2;

            sig1 = sig1!2 + sig2;

            Out.ar(ss.bus.master, sig1!2);
            
        }).add;

    }
);

)