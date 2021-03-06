p = Prophet08("Keyboard Uno", "USB Uno MIDI Interface"); c = p.gui;

c.view.remove

p.removeDependant(c)
p.dependants

p[5]
p[5] = 0


p.midiController
p.layerA.arpMode
p.layerA.osc1.freq = 90
p.layerA.osc2.fine = 15
p.layerA.osc1.keyboardOn = false
p.layerA.filter.freq = 50
p.layerA.slop = 5
p.layerA.seq1[0] = 20
p.layerA.seq1[1] = 50
p.layerA.seq2 = (0..10)
p.layerA.seq3 = (5..25)

p.layerA.unisonMode = 0

p.layerA.footCtrl.amt.value
p.layerA.modCtrl


c.view.oscView.noise.addDependant({ |...args| args.postcs; })

c.view.ids[100].addDependant({ |...args| args.postln})

p.layerA.seq1Dest = 8
p.layerA.seq2Dest = 4

c.view.ctrlView.amts[4].defaultValue = 0