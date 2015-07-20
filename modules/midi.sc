
(
~ss.loadModule(
    "midi", // module name
    ["midi"], // namespace hierarchy for module
    "Basic MIDI Controller Utility", // friendly name
    { arg ss, module; // module function...

        MIDIIn.connectAll;

        module.notes = Array.newClear(128);
        module.synth = "ss.spacey";

        MIDIdef.noteOn(\noteOn, {arg vel, midinote;
            module.notes[midinote] = Synth(module.synth, [
                \freq, (midinote + rand(0.2) - 0.1).midicps,
                \amp, vel.linexp(0, 127, 0.01, 0.69)
            ]);
            }
        );

        MIDIdef.noteOff(\noteOff, {arg vel, midinote;
            module.notes[midinote].release; // same as ~noteArray[midinote].set(\gate, 0);
        });

    }

);

)