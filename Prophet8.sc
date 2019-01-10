Prophet8 {
  var <inDevice, <outDevice, <>channel, <voice;

  *new { |deviceName, portName, channel = 0, makeDef = true, loadVoice = true|
    ^super.new.init(deviceName, portName, channel, makeDef, loadVoice);
  }

  init { |deviceName, portName, argChannel, makeDef, loadVoice|
    channel = argChannel;
    voice = PVoice();
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

  makeDef {
    // TODO
  }

  loadVoice {
    // TODO
  }


  prSendParam { |num, val|
    var numMSB = (num / 256).floor;
    var numLSB = num % 256;
    var valMSB = (val / 256).floor;
    var valLSB = val % 256;
    if (outDevice.notNil) {
      outDevice.control(channel, 0x63, numMSB);
      outDevice.control(channel, 0x62, numLSB);
      outDevice.control(channel, 0x06, valMSB);
      outDevice.control(channel, 0x26, valLSB);
    };
  }

  layerA { ^voice.layerA }
  layerB { ^voice.layerB }
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
    layerB = PLayer();
    splitPoint = 60; // 0-127, 60=C3
    keyboardMode = 0; // 0 normal, 1 stack, 2 split
    name = String.newFrom($ .dup(16));
  }
}

PLayer {
  var <oscs, <sync, <glideMode, <slop, <oscMix, <noiseAmt;
  var <filter, <vca, <lfos, <env3Dest, <env3;
  var <mods, <seq1Dest, <seq2Dest, <seq3Dest, <seq4Dest;
  var <modCtrl, <pressureCtrl, <breathCtrl, <velCtrl, <footCtrl;
  var <bpm, <clockDivide, <bendRange, <seqTrig, <keyPriorityMode, <unisonMode, <arpMode;
  var <env3RepearOn, <unisonOn, <arpOn, <gatedSeqOn;
  // unused 102-117
  // 118 and 119, split and keyboard mode in PVoice
  var seqs;
  // 184-199, name in PVoice

  *new {
    ^super.new.init();
  }

  init {
    oscs = 2.collect { POscillator() };
    sync = 0; // 0 off, 1 on
    glideMode = 0; // 0 fixed rate, 1 fixed rate auto, 2 fixed time, 3 fixed time auto
    slop = 0; // 0-5
    oscMix = 64; // 0-127
    noiseAmt = 0; // 0-127

    filter = PFilter();
    vca = PVCA();

    lfos = 4.collect { PLFO() };

    env3Dest = 0; // 0-43, see modulation destinations list
    env3 = PEnvelope();

    mods = 4.collect { PMod() };

    seq1Dest = seq2Dest = seq3Dest = seq4Dest = 0; // 0-43, see modulation destinations list

    modCtrl = PCtrl();
    pressureCtrl = PCtrl();
    breathCtrl = PCtrl();
    velCtrl = PCtrl();
    footCtrl = PCtrl();

    bpm = 120; // 30-250
    clockDivide = 0; // 0-12, see manual
    bendRange = 2; // 0-12, semitones (I assume)
    seqTrig = 0; // 0 normal, 1 normal no reset, 2 no gate, 3 no gate no reset, 4 key step
    keyPriorityMode = 5; // 0 low, 1 low retrig, 2 high, 3 high retrig, 4 last, 5 last retrig
    unisonMode = 0; // 0 one voice, 1 all voices, 2 all detune1, 3 all detune2, 4 all detune3
    arpMode = 0; // 0 up, 1 down, 2 up/down, 3 assign

    env3RepearOn = 0; // 0 off, 1 on
    unisonOn = 0; // 0 off, 1 on
    arpOn = 0; // 0 off, 1 on
    gatedSeqOn = 0; // 0 off, 1 on

    seqs = 4.collect { PSequence() };
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
}

PSequence {
  var steps;

  *new {
    ^super.new.init();
  }

  init {
    steps = 0.dup(16); // 0-125 normal sequence step value, 126 reset
  }
}
