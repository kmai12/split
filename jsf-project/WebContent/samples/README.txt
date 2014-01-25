The files in the "samples" folder are meant to be used as a starting point 
for your own JSF facelets (i.e., .xhtml) files, to save you from typing in 
the long and tedious DOCTYPE, <html...> declaration, and basic tags.

The the versions that say "with-comments" have some explanations inside the
files in HTML comments. Most of the time, you probably want to use the
other versions, that do not say "with-comments", because you do not want your 
final files to contain the comments. For each (with and without comments), 
there is a version for the case when you are making a form, and another 
version for the case where you have a pure output (results) page that contains
no form. The WebContent folder also has identical copies of the two template
files that do not have comments in them.

You have two main options: copying the files into your own JSF project,
or copying and renaming the entire jsf-blank project as described in
the notes.

A) If you make a new project of your own, copy the appropriate .xhtml file 
from jsf-blank/WebContent/samples into the WebContent 
(not the WebContent/samples) folder of your app. Rename the file to
give it a name that is meaningful for your app.
  Optional: also copy the "css" folder from jsf-blank/WebContent/ into 
the WebContent folder of your app. You can omit this step, but then you
won't have a style sheet, and your pages will not look the same as my
examples.

B) If you copy and rename the entire jsf-blank project, delete page-a.xhtml,
page-b.xhtml, and the entire samples folder. Rename sample-file-no-form.xhtml
and sample-file-with-form.xhtml to have meaningful names. Change index.jsp
to forward to the newly renamed file, instead of the old page-a.jsf.

Either way, this is simply supposed to save you from typing in the core
.xhtml tags. You still have to write all the Java code as described in the
tutorial.

Taken from http://www.coreservlets.com/JSF-Tutorial/jsf2/