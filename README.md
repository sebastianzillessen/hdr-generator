hdr-generator
=============


Image fusion of an exposure series is one possibility to generate High Dynamic Range Images (HDRI) without the need for special hardware. A HDRI has -- compared to common used images -- a higher ratio between the brightest and the darkest pixel and is therefore more capable to represent real scenes with high dynamic range.

The algorithm of Debevec and Malik used in this thesis estimates the response curve of the imaging process during the calculation of the HDRI by using an energy function.

We discuss a alternating approach for this algorithm which allows it to use all the pixels of the exposure series for the calculation of the HDRI. Three extensions for the used energy function are introduced:
An additional monotony constraint ensures that the estimated response curve is correcter under physical and radiometic aspects. The calculation of the radiance map is extended by a (spatial) smoothness term, which should lead to improvements in the output, especially when we consider noise in the input. In addition we replace the quadratic penalty terms in the algorithm by subquadratic functions. This should lead to an improvement regarding the robustness against measurement errors and outliers.

On top of the theoretical elaboration of the extensions an implementation in Java is presented. This software will be used to examine and evaluate the influence of the introdruced extensions.

This repository contains the bachelor-thesis itself and the software to generate HDR images out of a series of pictures with different exposure times. 



Example Files
-------------
The used example files are a series of different exposure times which were taken from
http://photography.tutsplus.com/tutorials/how-to-shoot-and-post-process-professional-hdr-photos-in-one-day-2--photo-4102 . This files are already registered and can be used as example.

External Libraries
-------------
- http://www.java2s.com/Code/Java/Swing-JFC/NumericTextField.htm
- https://code.google.com/p/metadata- extractor/
- http://www.adobe.com/devnet/xmp.html
- http://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html


Disclaimer
-------------
This work is part of a bachelor-thesis written at the **University of Stuttgart, Institute of Visualization and Interactive Systems**, *Universitätsstraße 38, 70569 Stuttgart, Germany*. The university has the complete legal right of use.


License
-------------
The software project is licensed under MIT.


The MIT License (MIT)

Copyright (c) 2013 Sebastian Zillessen

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
