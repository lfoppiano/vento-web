Camel Router WAR Project with Web Console and REST Support
==========================================================

This route should be run with at least 1024MB of heap space and 256 Mb
of PermGenSpace

MAVEN_OPTS=-Xmx1024m -XX:MaxPermSize=256m

To configure GATE you have to copy the files (from the gate-senti-brain):

test/resources/gate-project-classification/copy_comment_spans.jape
test/resources/gate-project-classification/annotationGrammar.jape
test/resources/gate-project-classification/batch-learning.classification.configuration.xml
test/resources/org/vento/semantic/sentiment/negative_adj_list.txt
test/resources/org/vento/semantic/sentiment/positive_adj_list.txt

into
/opt/local/gate

and
test/resources/vento-senti-brain

into
/opt/local/vento-senti-brain (check the permission, this should be writeable)