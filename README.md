## my-utils

Common Utilities i use
___

* [MyUtilsPackager](_MyUtilsPackager) packs selectively MyUtils classes into a jar (myutils.jar).
* [ANSI](help/ansi.md) Ansi  Console Color Helper  

- [sam.collection](src/sam/collection)
  - [IntList.java](src/sam/collection/IntList.java)    [test/usage](_testing/src/test/java/sam/collection/IntListTest.java) 
  - [IntSet.java](src/sam/collection/IntSet.java)  [test/usage](_testing/src/test/java/sam/collection/IntSetTest.java) 
  - [ArrayIterator.java](src/sam/collection/ArrayIterator.java) 
  - [ArraysUtils.java](src/sam/collection/ArraysUtils.java)  
  - [CollectionUtils.java](src/sam/collection/CollectionUtils.java)  
  - [FilteredIterator.java](src/sam/collection/FilteredIterator.java)  
  - [IndexedMap.java](src/sam/collection/IndexedMap.java)  
  - [IndexGetterIterator.java](src/sam/collection/IndexGetterIterator.java)  
  - [Iterables.java](src/sam/collection/Iterables.java)  
  - [Iterators.java](src/sam/collection/Iterators.java)  
  - [MappedIterator.java](src/sam/collection/MappedIterator.java)
  
- [sam.io.infile](src/sam/io/infile)
  - [InFile.java](src/sam/io/infile/InFile.java)  a mapped file. for Each write this returns [DataMeta](src/sam/io/infile/DataMeta.java) object.
  - [TextInFile.java](src/sam/io/infile/TextInFile.java) InFile with text read/write capability.
- [sam.io.serilizers](src/sam/io/serilizers)
  - [StringIOUtils.java](src/sam/io/serilizers/StringIOUtils.java) helpers to read/write text to file. [test/usage](_testing/src/test/java/sam/io/serilizers/StringIOUtilsTest.java)
  - [WriterImpl.java](src/sam/io/serilizers/WriterImpl.java) simple Writer (un-synchronized).   [test/usage](_testing/src/test/java/sam/io/serilizers/WriterImplTest.java)
  - [DataReader.java](src/sam/io/serilizers/DataReader.java) alternative for DataInputStream.   [test/usage](_testing/src/test/java/sam/io/serilizers/DataReaderWriterTest.java)
  - [DataWriter.java](src/sam/io/serilizers/DataWriter.java) alternative for DataOutputStream.   [test/usage](_testing/src/test/java/sam/io/serilizers/DataReaderWriterTest.java)

* [File/Path](help/files.md) Files Helper 
* [String](help/string.md)   String helper
Structures 
* [Config](help/config.md)
* [JavaFX](help/javafx.md)
* [Swing](help/swing.md)
* [JDBC](help/jdbc.md)
 
* [Extra](help/extra.md)

* [TSV](help/tsv.md) similar to CSV but values are separated using Tab(s)