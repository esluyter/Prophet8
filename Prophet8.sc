Prophet8 {
  var <inDevice, <outDevice, <globals, <voice, defKey;
  classvar layerParamKey, globalParamKey;

  *initClass {
    layerParamKey = [
     "osc/freq1", "osc/fine1", "osc/shape1", "osc/glide1", "osc/keyboardOn1",
     "osc/freq2", "osc/fine2", "osc/shape2", "osc/glide2", "osc/keyboardOn2",
     "osc/sync", "osc/glideMode", "osc/slop", "osc/mix", "osc/noise",
     "filter/freq", "filter/res", "filter/keyboardAmt", "filter/mod", "filter/poles",
     "filter/envAmt", "filter/velAmt", "filter/delay", "filter/attack",
     "filter/decay", "filter/sustain", "filter/release",
     "vca/initLevel", "vca/spread", "vca/volume",
     "vca/envAmt", "vca/velAmt", "vca/delay", "vca/attack",
     "vca/decay", "vca/sustain", "vca/release",
     "lfo/0/freq", "lfo/0/shape", "lfo/0/amt", "lfo/0/dest", "lfo/0/keySync",
     "lfo/1/freq", "lfo/1/shape", "lfo/1/amt", "lfo/1/dest", "lfo/1/keySync",
     "lfo/2/freq", "lfo/2/shape", "lfo/2/amt", "lfo/2/dest", "lfo/2/keySync",
     "lfo/3/freq", "lfo/3/shape", "lfo/3/amt", "lfo/3/dest", "lfo/3/keySync",
     "env3/dest", "env3/amt", "env3/velAmt", "env3/delay", "env3/attack",
     "env3/decay", "env3/sustain", "env3/release",
     "mod1Source", "mod1Amt", "mod1Dest",
     "mod2Source", "mod2Amt", "mod2Dest",
     "mod3Source", "mod3Amt", "mod3Dest",
     "mod4Source", "mod4Amt", "mod4Dest",
     "seq1Dest", "seq2Dest", "seq3Dest", "seq4Dest",
     "modAmt", "modDest", "pressureAmt", "pressureDest",
     "breathAmt", "breathDest", "velAmt", "velDest", "footAmt", "footDest",
     "top/bpm", "top/clockDivide", "top/bendRange", "top/seqTrig", "top/unisonMode", "top/unisonAssign",
     "top/arpMode", "env3/repeatOn", "osc/unisonOn", "top/arpOn", "top/gatedSeqOn"
   ] ++ (nil!16) ++ ["splitPoint", \keyboardMode] ++ (4.collect { |n| 16.collect { |i| ("seq" ++ (n + 1) ++ "Step" ++ (i + 1)) } }).flat;
   globalParamKey = [
     "program", "bank", "transpose", "fineTune", "channel", "polyChain", "midiClock",
     "localControl", "paramSend", "paramReceive", "programSendReceive", "pressureSendReceive",
     "controllerSendReceive", "sysexSendReceive", "pedalDest", "damperPolarity", "velCurve",
     "pressureCurve", "lcdContrast"
   ];
  }

  *new { |deviceName, portName, channel = 0, makeDef = true, loadVoice = true|
    ^super.new.init(deviceName, portName, channel, makeDef, loadVoice);
  }

  init { |deviceName, portName, argChannel, makeDef, loadVoice|
    globals = PGlobals();
    globals.channel = argChannel;
    globals.addDependant(this);
    voice = PVoice();
    voice.addDependant(this);
    if (deviceName.notNil) {
      this.makeDevice(deviceName, portName, makeDef, loadVoice);
    };
  }

  makeDevice { |deviceName, portName, makeDef = true, loadVoice = true|
    outDevice = MIDIOut.newByName(deviceName, portName);
    inDevice = MIDIIn.findPort(deviceName, portName);
    outDevice.latency = 0;
    if (makeDef) { this.makeDef };
    if (loadVoice) { this.loadVoice };
  }

  makeDef { |key = 'prophet'|
    var nrpn = 0!4;
    if (defKey.notNil) {
      MIDIdef(defKey).free;
    };
    defKey = key;
    MIDIdef.cc(key, { |val, num, channel|
      if ((channel == (globals.channel - 1)) || (globals.channel == 0)) {
        if (num == 0x63) {
          nrpn[0] = val;
        };
        if (num == 0x62) {
          nrpn[1] = val;
        };
        if (num == 0x06) {
          nrpn[2] = val;
        };
        if (num == 0x26) {
          var paramNum, paramVal;
          nrpn[3] = val;
          paramNum = nrpn[0] * 128 + nrpn[1];
          paramVal = nrpn[2] * 128 + nrpn[3];
          if (paramNum >= 384) {
            globals.prSet(paramNum, paramVal);
          } {
            voice.prSet(paramNum, paramVal);
          };
          this.prSetChanged(paramNum, paramVal);
        };
      };
    }, srcID: inDevice.uid);
  }

  loadVoice {
    // TODO
  }

  channel_ { |value|
    globals.channel = value;
    this.makeDef(defKey);
  }


  prSendParam { |num, val|
    var channel = if (globals.channel == 0) { 0 } { globals.channel - 1 };
    var numMSB = (num / 128).floor;
    var numLSB = num % 128;
    var valMSB = (val / 128).floor;
    var valLSB = val % 128;
    if (outDevice.notNil) {
      outDevice.control(channel, 0x63, numMSB);
      outDevice.control(channel, 0x62, numLSB);
      outDevice.control(channel, 0x06, valMSB);
      outDevice.control(channel, 0x26, valLSB);
    } {
      [numMSB, numLSB, valMSB, valLSB].postln;
    };
    this.prSetChanged(num, val);
  }

  update { |o, num, val|
    this.prSendParam(num, val);
  }

  prSetChanged { |num, val|
    var key = case
      { num <= 183 } {
        "layerA/" ++ layerParamKey[num];
      }
      { num <= 199 } {
        "name/" ++ (num - 184);
      }
      { num <= 383 } {
        "layerB/" ++ layerParamKey[num - 200];
      }
      { true } {
        globalParamKey[num - 384];
      };
    this.changed(key, val);
  }

  layerA { ^voice.layerA }
  layerB { ^voice.layerB }
}

PGlobals {
  var <program, <bank, <transpose, <fineTune, <channel, <polyChain, <midiClock,
    <localControl, <paramSend, <paramReceive, <programSendReceive, <pressureSendReceive,
    <controllerSendReceive, <sysexSendReceive, <pedalDest, <damperPolarity, <velCurve,
    <pressureCurve, <lcdContrast;

  *new {
    ^super.new.init();
  }

  init {
    program = 0; // 0-127
    bank = 0; // 0-1
    transpose = 12; // 0-24, -12 to 12
    fineTune = 50; // 0-100, -50 to 50
    channel = 1; // 0 receive on all channels, 1-16 is channel number
    polyChain = 0; // 0 no chaining, 1 poly chain out, 2 poly chain in
    midiClock = 0; // 0 internal don't send, 1 internal send, 2 clock in, 3 clock in retransmit
    localControl = 0; // 0-1
    paramSend = 0; // 0 NRPN, 1 CC, 2 off
    paramReceive = 0; // 0 all, 1 NRPN, 2 CC, 3 off
    programSendReceive = 1; // 0-1
    pressureSendReceive = 1; // 0-1
    controllerSendReceive = 1; // 0-1
    sysexSendReceive = 1; // 0-1
    pedalDest = 0; // 0 foot, 1 breath, 2 expression, 3 volume, 4 filter freq, 5 filter freq/2
    damperPolarity = 0; // 0 normally open, 1 normally closed
    velCurve = 0; // 0-3
    pressureCurve = 0; // 0-3
    lcdContrast = 0;
  }

  prSet { |num, val|
    switch(num,
      384, { transpose = val },
      385, { fineTune = val },
      386, { channel = val },
      387, { polyChain = val },
      388, { midiClock = val },
      389, { localControl = val },
      390, { paramSend = val },
      391, { paramReceive = val },
      392, { programSendReceive = val },
      393, { pressureSendReceive = val },
      394, { controllerSendReceive = val },
      395, { sysexSendReceive = val },
      396, { pedalDest = val },
      397, { damperPolarity = val },
      398, { velCurve = val },
      399, { pressureCurve = val },
    );
  }

  transpose_ { |value|
    transpose = value;
    this.changed(384, value);
  }
  fineTune_ { |value|
    fineTune = value;
    this.changed(385, value);
  }
  channel_ { |value|
    channel = value;
    this.changed(386, value);
  }
  polyChain_ { |value|
    polyChain = value;
    this.changed(387, value);
  }
  midiClock_ { |value|
    midiClock = value;
    this.changed(388, value);
  }
  localControl_ { |value|
    localControl = value;
    this.changed(389, value);
  }
  paramSend_ { |value|
    paramSend = value;
    this.changed(390, value);
  }
  paramReceive_ { |value|
    paramReceive = value;
    this.changed(391, value);
  }
  programSendReceive_ { |value|
    programSendReceive = value;
    this.changed(392, value);
  }
  pressureSendReceive_ { |value|
    pressureSendReceive = value;
    this.changed(393, value);
  }
  controllerSendReceive_ { |value|
    controllerSendReceive = value;
    this.changed(394, value);
  }
  sysexSendReceive_ { |value|
    sysexSendReceive = value;
    this.changed(395, value);
  }
  pedalDest_ { |value|
    pedalDest = value;
    this.changed(396, value);
  }
  damperPolarity_ { |value|
    damperPolarity = value;
    this.changed(397, value);
  }
  velCurve_ { |value|
    velCurve = value;
    this.changed(398, value);
  }
  pressureCurve_ { |value|
    pressureCurve = value;
    this.changed(399, value);
  }
}

PVoice {
  var <layerA, <layerB;
  var <splitPoint, <keyboardMode;
  var name;

  *new {
    ^super.new.init();
  }

  init {
    layerA = PLayer();
    layerA.addDependant({ |o, num, val| this.changed(num, val); });
    layerB = PLayer();
    layerB.addDependant({ |o, num, val| this.changed(num + 200, val); });
    splitPoint = 60; // 0-127, 60=C3
    keyboardMode = 0; // 0 normal, 1 stack, 2 split
    name = String.newFrom($ .dup(16));
  }

  prSet { |num, val|
    if (num >= 200) {
      layerB.prSet(num - 200, val);
    } {
      if (num == 118) {
        splitPoint = val;
      } {
        if (num == 119) {
          keyboardMode = val;
        } {
          layerA.prSet(num, val);
        };
      };
    }
  }

  splitPoint_ { |value|
    splitPoint = value;
    this.changed(118, value);
  }
  keyboardMode_ { |value|
    keyboardMode = value;
    this.changed(119, value);
  }

  // TODO update name with changed flag
}

PLayer {
  var <oscs, <sync, <glideMode, <slop, <oscMix, <noiseAmt;
  var <filter, <vca, <lfos, <env3Dest, <env3;
  var <mods, <seq1Dest, <seq2Dest, <seq3Dest, <seq4Dest;
  var <modCtrl, <pressureCtrl, <breathCtrl, <velCtrl, <footCtrl;
  var <bpm, <clockDivide, <bendRange, <seqTrig, <keyPriorityMode, <unisonMode, <arpMode;
  var <env3RepeatOn, <unisonOn, <arpOn, <gatedSeqOn;
  // unused 102-117
  // 118 and 119, split and keyboard mode in PVoice
  var seqs;
  // 184-199, name in PVoice

  *new {
    ^super.new.init();
  }

  init {
    oscs = 2.collect { |i|
      var osc = POscillator();
      osc.addDependant({ |o, num, val| this.changed(num + (i * 5), val); });
      osc;
    };
    sync = 0; // 0 off, 1 on
    glideMode = 0; // 0 fixed rate, 1 fixed rate auto, 2 fixed time, 3 fixed time auto
    slop = 0; // 0-5
    oscMix = 64; // 0-127
    noiseAmt = 0; // 0-127

    filter = PFilter();
    filter.addDependant({ |o, num, val| this.changed(num + 15, val); });
    vca = PVCA();
    vca.addDependant({ |o, num, val| this.changed(num + 27, val); });

    lfos = 4.collect { |i|
      var lfo = PLFO();
      lfo.addDependant({ |o, num, val| this.changed(num + 37 + (i * 5), val); });
      lfo;
    };

    env3Dest = 0; // 0-43, see modulation destinations list
    env3 = PEnvelope();
    env3.addDependant({ |o, num, val| this.changed(num + 58, val); });

    mods = 4.collect { |i|
      var mod = PMod();
      mod.addDependant({ |o, num, val| this.changed(num + 65 + (i * 3), val); });
    };

    seq1Dest = seq2Dest = seq3Dest = seq4Dest = 0; // 0-43, see modulation destinations list

    modCtrl = PCtrl();
    pressureCtrl = PCtrl();
    breathCtrl = PCtrl();
    velCtrl = PCtrl();
    footCtrl = PCtrl();
    [modCtrl, pressureCtrl, breathCtrl, velCtrl, footCtrl].do { |ctrl, i|
      ctrl.addDependant({ |o, num, val| this.changed(num + 81 + (i * 2), val) });
    };

    bpm = 120; // 30-250
    clockDivide = 0; // 0-12, see manual
    bendRange = 2; // 0-12, semitones (I assume)
    seqTrig = 0; // 0 normal, 1 normal no reset, 2 no gate, 3 no gate no reset, 4 key step
    keyPriorityMode = 5; // 0 low, 1 low retrig, 2 high, 3 high retrig, 4 last, 5 last retrig
    unisonMode = 0; // 0 one voice, 1 all voices, 2 all detune1, 3 all detune2, 4 all detune3
    arpMode = 0; // 0 up, 1 down, 2 up/down, 3 assign

    env3RepeatOn = 0; // 0 off, 1 on
    unisonOn = 0; // 0 off, 1 on
    arpOn = 0; // 0 off, 1 on
    gatedSeqOn = 0; // 0 off, 1 on

    seqs = 4.collect { PSequence() }; // TODO add dependant
  }

  prSet { |num, val|
    if (switch(num,
      10, { sync = val },
      11, { glideMode = val },
      12, { slop = val },
      13, { oscMix = val },
      14, { noiseAmt = val },
      57, { env3Dest = val },
      77, { seq1Dest = val },
      78, { seq2Dest = val },
      79, { seq3Dest = val },
      80, { seq4Dest = val },
      91, { bpm = val },
      92, { clockDivide = val },
      93, { bendRange = val },
      94, { seqTrig = val },
      95, { unisonMode = val },
      96, { keyPriorityMode = val },
      97, { arpMode = val },
      98, { env3RepeatOn = val },
      99, { unisonOn = val },
      100, { arpOn = val },
      101, { gatedSeqOn = val }
    ).isNil) {
      case
        { num <= 4 } { oscs[0].prSet(num, val) }
        { num <= 9 } { oscs[1].prSet(num - 5, val) }
    };
  }

  sync_ { |value|
    sync = value;
    this.changed(10, value);
  }
  glideMode_ { |value|
    glideMode = value;
    this.changed(11, value);
  }
  slop_ { |value|
    slop = value;
    this.changed(12, value);
  }
  oscMix_ { |value|
    oscMix = value;
    this.changed(13, value);
  }
  noiseAmt_ { |value|
    noiseAmt = value;
    this.changed(14, value);
  }
  env3Dest_ { |value|
    env3Dest = value;
    this.changed(57, value);
  }
  seq1Dest_ { |value|
    seq1Dest = value;
    this.changed(77, value);
  }
  seq2Dest_ { |value|
    seq2Dest = value;
    this.changed(78, value);
  }
  seq3Dest_ { |value|
    seq3Dest = value;
    this.changed(79, value);
  }
  seq4Dest_ { |value|
    seq4Dest = value;
    this.changed(80, value);
  }
  bpm_ { |value|
    bpm = value;
    this.changed(91, value);
  }
  clockDivide_ { |value|
    clockDivide = value;
    this.changed(92, value);
  }
  bendRange_ { |value|
    bendRange = value;
    this.changed(93, value);
  }
  seqTrig_ { |value|
    seqTrig = value;
    this.changed(94, value);
  }
  keyPriorityMode_ { |value|
    keyPriorityMode = value;
    this.changed(96, value); // SWAPPED vis a vis manual
  }
  unisonMode_ { |value|
    unisonMode = value;
    this.changed(95, value); // SWAPPED vis a vis manual
  }
  arpMode_ { |value|
    arpMode = value;
    this.changed(97, value);
  }
  env3RepeatOn_ { |value|
    env3RepeatOn = value;
    this.changed(98, value);
  }
  unisonOn_ { |value|
    unisonOn = value;
    this.changed(99, value);
  }
  arpOn_ { |value|
    arpOn = value;
    this.changed(100, value);
  }
  gatedSeqOn_ { |value|
    gatedSeqOn = value;
    this.changed(101, value);
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

POscillator {
  var <freq, <fine, <shape, <glide, <keyboardOn;

  *new {
    ^super.new.init();
  }

  init {
    freq = 60; // 0-120, in semitones (10 octaves)
    fine = 50; // 0-100, -50 - 50 cents
    shape = 1; // 0 off, 1 saw, 2 tri, 3 saw/tri mix, 4-103 pwm
    glide = 0; // 0-127
    keyboardOn = 1; // 0 off, 1 on
  }

  prSet { |num, val|
    switch(num,
      0, { freq = val },
      1, { fine = val },
      2, { shape = val },
      3, { glide = val },
      4, { keyboardOn = val }
    );
  }

  freq_ { |value|
    freq = value;
    this.changed(0, value);
  }
  fine_ { |value|
    fine = value;
    this.changed(1, value);
  }
  shape_ { |value|
    shape = value;
    this.changed(2, value);
  }
  glide_ { |value|
    glide = value;
    this.changed(3, value);
  }
  keyboardOn_ { |value|
    keyboardOn = value;
    this.changed(4, keyboardOn);
  }
}

PFilter {
  var <freq, <res, <keyboardAmt, <mod, <poles, <env;

  *new {
    ^super.new.init();
  }

  init {
    freq = 164; // 0-164, semitones
    res = 0; // 0-127
    keyboardAmt = 0; // 0-127
    mod = 0; // 0-127
    poles = 0; // 0 two, 1 four
    env = PEnvelope();
    env.addDependant({ |o, num, val| this.changed(num + 5, val); });
  }

  freq_ { |value|
    freq = value;
    this.changed(0, value);
  }
  res_ { |value|
    res = value;
    this.changed(1, value);
  }
  keyboardAmt_ { |value|
    keyboardAmt = value;
    this.changed(2, value);
  }
  mod_ { |value|
    mod = value;
    this.changed(3, value);
  }
  poles_ { |value|
    poles = value;
    this.changed(4, value);
  }
}

PVCA {
  var <initLevel, <spread, <volume, <env;

  *new {
    ^super.new.init();
  }

  init {
    initLevel = 0; // 0-127
    spread = 0; // 0-127
    volume = 127; // 0-127
    env = PEnvelope(); // note this envelope only has amount from 0 to 127
    env.addDependant({ |o, num, val| this.changed(num + 3, val); });
  }

  initLevel_ { |value|
    initLevel = value;
    this.changed(0, value);
  }
  spread_ { |value|
    spread = value;
    this.changed(1, value);
  }
  volume_ { |value|
    volume = value;
    this.changed(2, value);
  }
}

PEnvelope {
  var <amt, <velAmt, <delay, <attack, <decay, <sustain, <release;

  *new {
    ^super.new.init();
  }

  init {
    amt = 127; // 0-254, -127 to 127, except VCA env which is just 0-127 >:/
    velAmt = 0; // 0-127
    delay = 0; // 0-127
    attack = 0; // 0-127
    decay = 0; // 0-127
    sustain = 127; // 0-127
    release = 0; // 0-127
  }

  amt_ { |value|
    amt = value;
    this.changed(0, value);
  }
  velAmt_ { |value|
    velAmt = value;
    this.changed(1, value);
  }
  delay_ { |value|
    delay = value;
    this.changed(2, value);
  }
  attack_ { |value|
    attack = value;
    this.changed(3, value);
  }
  decay_ { |value|
    decay = value;
    this.changed(4, value);
  }
  sustain_ { |value|
    sustain = value;
    this.changed(5, value);
  }
  release_ { |value|
    release = value;
    this.changed(6, value);
  }
}

PLFO {
  var <freq, <shape, <amt, <dest, <keySync;

  *new {
    ^super.new.init();
  }

  init {
    freq = 0; // 0-166; 0-150 unsynced freq, 151-166 see manual
    shape = 0; // 0 tri, 1 rev saw, 2 saw, 3 square, 4 random
    amt = 0; // 0-127
    dest = 0; // 0-43, see modulation destinations list
    keySync = 0; // 0 off, 1 on
  }

  freq_ { |value|
    freq = value;
    this.changed(0, value);
  }
  shape_ { |value|
    shape = value;
    this.changed(1, value);
  }
  amt_ { |value|
    amt = value;
    this.changed(2, value);
  }
  dest_ { |value|
    dest = value;
    this.changed(3, value);
  }
  keySync_ { |value|
    keySync = value;
    this.changed(4, value);
  }
}

PMod {
  var <source, <amt, <dest;

  *new {
    ^super.new.init();
  }

  init {
    source = 0; // 0-20, see modulation source list
    amt = 0; // 0-254, -127 to 127
    dest = 0; // 0-43, see modulation destinations list
  }

  source_ { |value|
    source = value;
    this.changed(0, value);
  }
  amt_ { |value|
    amt = value;
    this.changed(1, value);
  }
  dest_ { |value|
    dest = value;
    this.changed(2, value);
  }
}

PCtrl {
  var <amt, <dest;

  *new {
    ^super.new.init();
  }

  init {
    amt = 0; // 0-254, -127 to 127
    dest = 0; // 0-43, see modulation destinations list
  }

  amt_ { |value|
    amt = value;
    this.changed(0, value);
  }
  dest_ { |value|
    dest = value;
    this.changed(1, value);
  }
}

PSequence {
  var steps;

  *new {
    ^super.new.init();
  }

  init {
    steps = 0.dup(16); // 0-125 normal sequence step value, 126 reset
  }

  // TODO assign steps with changed flag
}
