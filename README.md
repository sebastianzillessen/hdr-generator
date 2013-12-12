hdr-generator
=============



High Dynamic Range (HDR) image technology has gained ever more importance in the thriving field of Computional Photography. HDR images allow for highly detailed display of lighting contrasts.
In this context, the ration between the brightest and darkest pixels is referred to as dynamic range. It is possible to compose images with highly increased dynamic range independent of dedicated hardware by merging single pictures of a series.
This dissertation first discusses the method developed by Debevec and Malik which estimates the device specific response curve using an energy function in order to calculate the HDR image. The author then offers an alternating method, which allows for calculating the HDR image over all the pixels of an image series. Furthermore, three additions to the discussed energy formula will be proposed: Introducing a monotony condition to the response curve improves the physical and radiometric accuracy. Adding a (spatial) smoothness term to the calculation of the radiance map improves output, especially when handling noise in the input. Finally, replacing the quadric penalty term in the in the algorithm with a subquadric function which will improve robustness against measurement errors and outliers.
In addition to the theoretical discussion on improving the energy formula, a Java imple- mentation is presented. Using this software, the impact of the proposed additions were experimentally tested and evaluated.



This repository contains the bachelor-thesis itself and the software to generate HDR images out of a series of pictures with different exposure times. 



Example Files
-------------
The used example files are a series of different exposure times which were taken from
http://photography.tutsplus.com/tutorials/how-to-shoot-and-post-process-professional-hdr-photos-in-one-day-2--photo-4102 . This files are already registered and can be used as example.

External Libraries
-------------
- http://www.java2s.com/Code/Java/Swing-JFC/NumericTextField.htm
- https://code.google.com/p/metadata-extractor/
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
