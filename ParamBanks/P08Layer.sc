P08Layer : P08ParamBank {
  var <oscs, <sync, <glideMode, <slop, <oscMix, <noiseAmt;
  var <filter, <vca, <lfos, <env3Dest, <env3;
  var <mods, <seq1Dest, <seq2Dest, <seq3Dest, <seq4Dest;
  var <modCtrl, <pressureCtrl, <breathCtrl, <velCtrl, <footCtrl;
  var <bpm, <clockDivide, <bendRange, <seqTrig, <keyPriorityMode, <unisonMode, <arpMode;
  var <env3RepeatOn, <unisonOn, <arpOn, <gatedSeqOn;
  // unused 102-117
  // 118 and 119, split and keyboard mode in PVoice
  var <seqs;

  init { |offset|
    oscs = 2.collect { |i| P08Osc(offset + (i * 5)) };
    sync = P08Bool(offset + 10);
    glideMode = P08Param(offset + 11, 0, 3); // 0 fixed rate, 1 fixed rate auto, 2 fixed time, 3 fixed time auto
    slop = P08Param(offset + 12, 0, 5);
    oscMix = P08Param(offset + 13, value: 64);
    noiseAmt = P08Param(offset + 14);

    filter = P08Filter(offset + 15);
    vca = P08VCA(offset + 27);

    lfos = 4.collect { |i| P08LFO(offset + 37 + (i * 5)) };

    env3Dest = P08Param(offset + 57, 0, 43); // see modulation destinations list
    env3 = P08Env(offset + 58);

    mods = 4.collect { |i| P08Mod(offset + 65 + (i * 3)) };

    seq1Dest = P08Param(offset + 77, 0, 43);
    seq2Dest = P08Param(offset + 78, 0, 43);
    seq3Dest = P08Param(offset + 79, 0, 43);
    seq4Dest = P08Param(offset + 80, 0, 43); // 0-43, see modulation destinations list

    modCtrl = P08Ctrl(offset + 81);
    pressureCtrl = P08Ctrl(offset + 83);
    breathCtrl = P08Ctrl(offset + 85);
    velCtrl = P08Ctrl(offset + 87);
    footCtrl = PCtrl(offset + 89);

    bpm = P08Param(offset + 91, 30, 250, 120);
    clockDivide = P08Param(offset + 92, 0, 12); // see manual
    bendRange = P08Param(offset + 93, 0, 12, 2); // semitones
    seqTrig = P08Param(offset + 94, 0, 4); // 0 normal, 1 normal no reset, 2 no gate, 3 no gate no reset, 4 key step
    keyPriorityMode = P08Param(offset + 95, 0, 5, 5); // 0 low, 1 low retrig, 2 high, 3 high retrig, 4 last, 5 last retrig
    unisonMode = P08Param(offset + 96, 0, 4); // 0 one voice, 1 all voices, 2 all detune1, 3 all detune2, 4 all detune3
    arpMode = P08Param(offset + 97, 0, 3); // 0 up, 1 down, 2 up/down, 3 assign

    env3RepeatOn = P08Bool(offset + 98);
    unisonOn = P08Bool(offset + 99);
    arpOn = P08Bool(offset + 100);
    gatedSeqOn = P08Bool(offset + 101);

    seqs = 4.collect { |i| P08Seq(offset + 120 + (i * 16)) };
  }

  osc1 { ^oscs[0] }
  osc2 { ^oscs[1] }
  lfo1 { ^lfos[0] }
  lfo2 { ^lfos[1] }
  lfo3 { ^lfos[2] }
  lfo4 { ^lfos[3] }
  mod1 { ^mods[0] }
  mod2 { ^mods[1] }
  mod3 { ^mods[2] }
  mod4 { ^mods[3] }
  seq1 { ^seqs[0] }
  seq2 { ^seqs[1] }
  seq3 { ^seqs[2] }
  seq4 { ^seqs[3] }
}
