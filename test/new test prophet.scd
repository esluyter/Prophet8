p = Prophet08()

p[5]
p[5] = 0


p.midiController

p.addDependant({ |object, param| param.postln; });
p.layerB.osc1.freq = 90
p.layerA.osc2.fine = 15
p.layerA.osc1.keyboardOn = false
p.layerA.filter.freq = 50
p.layerA.slop = 5
p.layerA.seq1[0] = 20
p.layerA.seq1[1] = 50
p.layerA.seq2 = (0..10)
p.layerA.seq3 = (5..25)
