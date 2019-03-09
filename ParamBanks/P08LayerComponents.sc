P08Osc : P08ParamBank {
  var <freq, <fine, <shape, <glide, <keyboardOn;

  init { |offset|
    freq = P08Param(offset, 0, 120, 60); // in semitones (10 octaves)
    fine = P08Param(offset + 1, -50, 50); // in cents
    shape = P08Param(offset + 2, 0, 103, 1); // 0 off, 1 saw, 2 tri, 3 saw/tri mix, 4-103 pwm
    glide = P08Param(offset + 3);
    keyboardOn = P08Bool(offset + 4, true);
  }
}

P08Filter : P08ParamBank {
  var <freq, <res, <keyboardAmt, <mod, <fourPole, <env;

  init { |offset|
    freq = P08Param(offset, 0, 164, 164); // in semitones
    res = P08Param(offset + 1);
    keyboardAmt = P08Param(offset + 2);
    mod = P08Param(offset + 3);
    fourPole = P08Bool(offset + 4); // 0 two, 1 four
    env = P08Env(offset + 5);
  }
}

P08VCA : P08ParamBank {
  var <initLevel, <spread, <volume, <env;

  init { |offset|
    initLevel = P08Param(offset);
    spread = P08Param(offset + 1);
    volume = P08Param(offset + 2, value: 127);
    env = P08Env(offset + 3, minAmt: 0); // note this envelope only has amount from 0 to 127
  }
}

P08Env : P08ParamBank {
  var <amt, <velAmt, <delay, <attack, <decay, <sustain, <release;

  *new { |offset = 0, minAmt = -127|
    ^super.prNewArgs(offset, minAmt);
  }

  init { |offset, minAmt|
    amt = P08Param(offset, minAmt, 127);
    velAmt = P08Param(offset + 1);
    delay = P08Param(offset + 2);
    attack = P08Param(offset + 3);
    decay = P08Param(offset + 4);
    sustain = P08Param(offset + 5, value: 127);
    release = P08Param(offset + 6);
  }
}

P08LFO : P08ParamBank {
  var <freq, <shape, <amt, <dest, <keySync;

  init { |offset|
    freq = P08Param(offset, 0, 166); // 0-150 unsynced freq, 151-166 see manual
    shape = P08Param(offset + 1, 0, 4); // 0 tri, 1 rev saw, 2 saw, 3 square, 4 random
    amt = P08Param(offset + 2);
    dest = P08Param(offset + 3, 0, 43); // see modulation destinations list
    keySync = P08Bool(offset + 4);
  }
}

P08Mod : P08ParamBank {
  var <source, <amt, <dest;

  init { |offset|
    source = P08Param(offset, 0, 20); // see modulation source list
    amt = P08Param(offset + 1, -127, 127);
    dest = P08Param(offset + 2, 0, 43); // see modulation destinations list
  }
}

P08Ctrl : P08ParamBank {
  var <amt, <dest;

  init { |offset|
    amt = P08Param(offset, -127, 127);
    dest = P08Param(offset + 1, 0, 43); // see modulation destinations list
  }
}

P08Seq : P08ParamBank {
  var <steps;

  init { |offset|
    steps = { |i| P08Param(offset + i) }.dup(16);
  }
}
