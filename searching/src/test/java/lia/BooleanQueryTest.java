package lia;

import lia.common.TestUtil;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BooleanQueryTest
{
  @Test
  void testAnd() throws Exception {
    TermQuery searchingBooks = new TermQuery(new Term("subject", "search"));
    Query books2010 = LongPoint.newRangeQuery("pubmonth", 201001, 201012);

    BooleanQuery searchingBooks2010 = new BooleanQuery.Builder()
        .add(new BooleanClause(searchingBooks, Occur.MUST))
        .add(new BooleanClause(books2010, Occur.MUST))
        .build();

    Directory directory = TestUtil.getBookIndexDirectory();
    IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
    TopDocs matches = searcher.search(searchingBooks2010, 10);

    assertTrue(TestUtil.hitsIncludeTitle(searcher, matches, "Lucene in Action, Second Edition"));

    directory.close();
  }

  @Test
  void testOr() throws Exception {
    TermQuery methodologyBooks = new TermQuery(new Term("category", "/technology/computers/programming/methodology"));
    TermQuery easternPhilosophyBooks = new TermQuery(new Term("category", "/philosophy/eastern"));

    BooleanQuery enlightenmentBooks = new BooleanQuery.Builder()
        .add(methodologyBooks, Occur.SHOULD)
        .add(easternPhilosophyBooks, Occur.SHOULD)
        .build();

    Directory directory = TestUtil.getBookIndexDirectory();
    IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
    TopDocs matches = searcher.search(enlightenmentBooks, 100);

    assertTrue(TestUtil.hitsIncludeTitle(searcher, matches, "Extreme Programming Explained"));
    assertTrue(TestUtil.hitsIncludeTitle(searcher, matches, "Tao Te Ching \u9053\u5FB7\u7D93"));

    directory.close();
  }
}