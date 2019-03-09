P08Globals : P08ParamBank {
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

    transpose = P08Param(384, -12, 12);
    fineTune = P08Param(385, -50, 50);
    channel = P08Param(386, 0, 16); // 0 receive on all channels, 1-16 is channel number
    polyChain = P08Param(387, 0, 2); // 0 no chaining, 1 poly chain out, 2 poly chain in
    midiClock = P08Param(388, 0, 3); // 0 internal don't send, 1 internal send, 2 clock in, 3 clock in retransmit
    localControl = P08Bool(389);
    paramSend = P08Param(390, 0, 2); // 0 NRPN, 1 CC, 2 off
    paramReceive = P08Param(391, 0, 3); // 0 all, 1 NRPN, 2 CC, 3 off
    programSendReceive = P08Bool(392, true);
    pressureSendReceive = P08Bool(393, true);
    controllerSendReceive = P08Bool(394, true);
    sysexSendReceive = P08Bool(395, true);
    pedalDest = P08Param(396, 0, 5); // 0 foot, 1 breath, 2 expression, 3 volume, 4 filter freq, 5 filter freq/2
    damperPolarity = P08Bool(397); // 0 normally open, 1 normally closed
    velCurve = P08Param(398, 0, 3);
    pressureCurve = P08Param(399, 0, 3);

    lcdContrast = 0;
  }
}
