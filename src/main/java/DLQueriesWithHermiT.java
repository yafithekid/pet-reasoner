import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import uk.ac.manchester.cs.owl.owlapi.OWLDataPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

@SuppressWarnings({"Duplicates"})
public class DLQueriesWithHermiT {
    static OWLOntology ONTOLOGY;
    static int AIR = 1;
    static int DARAT = 2;

    static int AKUARIUM = 1;
    static int KOLAM = 2;

    static int LAPANGAN = 1;
    static int PEKARANGAN = 2;
    static int RUMAH = 3;

    static int KOMPETISI = 1;
    static int PEKERJA_PENJAGA = 2;
    static int TERNAK = 3;
    static int TRANSPORTASI = 4;

    static int BEBAS = 0;

    public static void main(String[] args) throws Exception {
        // Load an example ontology.
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager
                .loadOntologyFromOntologyDocument(new File(System.getProperty("user.dir").concat(File.separator).concat("pet.owl")));
        if (ONTOLOGY == null){
            ONTOLOGY = ontology;
        }
        // We need a reasoner to do our query answering


        // These two lines are the only relevant difference between this code and the original example
        // This example uses HermiT: http://hermit-reasoner.com/
        OWLReasoner reasoner = new Reasoner.ReasonerFactory().createReasoner(ontology);



        ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        // Create the DLQueryPrinter helper class. This will manage the
        // parsing of input and printing of results
        DLQueryPrinter dlQueryPrinter = new DLQueryPrinter(new DLQueryEngine(reasoner,
                shortFormProvider), shortFormProvider);
        // Enter the query loop. A user is expected to enter class
        // expression on the command line.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));


        System.out.println("Sistem Pakar Hewan Peliharaan");
        System.out.println("-----------------------------");

        do {
            QueryBuilder qb = new QueryBuilder();

            int ruang = askRuang(br);
            if (ruang == DARAT){
                qb.darat();
                int darat = askDarat(br);
                if (darat == LAPANGAN){
                    qb.lapangan();
                } else if (darat == PEKARANGAN){
                    qb.pekarangan();
                } else if (darat == RUMAH){
                    qb.rumah();
                }
            } else if (ruang == AIR){
                int air = askAir(br);
                if (air == AKUARIUM){
                    qb.akuarium();
                } else if (air == KOLAM){
                    qb.kolam();
                }
            }
            int fungsi = askTujuan(br);
            if (fungsi == TERNAK){
                qb.ternak();
            } else if (fungsi == TRANSPORTASI){
                qb.transportasi();
            } else if (fungsi == KOMPETISI){
                qb.kompetisi();
            } else if (fungsi == PEKERJA_PENJAGA){
                qb.pekerjaPenjaga();
            }

//            if (askQuestion("Apakah hewan yang anda cari untuk kompetisi?", br)) qb.kompetisi();
//            if (askQuestion("Apakah hewan yang anda cari sebagai hewan pekerja?", br)) qb.pekerja();
//            if (askQuestion("Apakah hewan yang anda cari sebagai hewan transportasi?", br)) qb.transportasi();
//
//            if (askQuestion("Apakah hewan yang anda cari akan menempati rumah?", br)) qb.rumah();
//            else if (askQuestion("Apakah hewan yang anda cari akan menempati lapangan?", br)) qb.lapangan();

            dlQueryPrinter.askQuery(qb.build());

            System.out.println("Ingin ulang dari awal? (Y/N)");
        }while (br.readLine().equalsIgnoreCase("Y"));

        /*
        //contoh
        //hewan yang kompetisi dan lapangan
        QueryBuilder qb = new QueryBuilder();
        qb.kompetisi();
        qb.lapangan();
        dlQueryPrinter.askQuery(qb.build());
        //hewan yang rumah dan kompetisi
        qb = new QueryBuilder();
        qb.kompetisi();
        qb.rumah();
        dlQueryPrinter.askQuery(qb.build());


        while (true) {
            System.out
                    .println("Type a class expression in Manchester Syntax and press Enter (or press x to exit):");
            String classExpression = br.readLine();
            // Check for exit condition
            if (classExpression == null || classExpression.equalsIgnoreCase("x")) {
                break;
            }
            dlQueryPrinter.askQuery(classExpression.trim());
            System.out.println();
        }*/
    }

    private static boolean askQuestion(String question, BufferedReader reader){
        System.out.println(question);
        try {
            if (reader.readLine().equals("Y")){
                return true;
            }
            return  false;
        } catch (IOException e) {
            return  false;
        }
    }

    private static int askRuang(BufferedReader br){
        System.out.println("Hewan yang tinggal di mana?");
        System.out.println(AIR+". Air");
        System.out.println(DARAT+". Darat");
        System.out.println(BEBAS+". Bebas");
        System.out.print("Pilihan: ");
        try {
            String s = br.readLine();
            int result = Integer.parseInt(s);
            if (result < 1 || result > 2){
                result = 0;
            }
            return result;
        } catch (IOException e) {
            return 0;
        }
    }

    private static int askAir(BufferedReader br){
        System.out.println("Pilihan air:");
        System.out.println(AKUARIUM+". Akuarium");
        System.out.println(KOLAM+". Kolam");
        System.out.println(BEBAS+". Bebas");
        System.out.print("Pilihan: ");
        try {
            String s = br.readLine();
            int result = Integer.parseInt(s);
            if (result < 1 || result > 2){
                result = 0;
            }
            return result;
        } catch (IOException e) {
            return 0;
        }
    }

    private static int askDarat(BufferedReader br){
        System.out.println("Pilihan darat:");
        System.out.println(LAPANGAN+". Lapangan");
        System.out.println(PEKARANGAN+". Pekarangan");
        System.out.println(RUMAH+". Rumah");
        System.out.println(BEBAS+". Bebas");
        System.out.print("Pilihan: ");
        try {
            String s = br.readLine();
            int result = Integer.parseInt(s);
            if (result < 1 || result > 3){
                result = 0;
            }
            return result;
        } catch (IOException e) {
            return 0;
        }
    }

    private static int askTujuan(BufferedReader br){
        System.out.println("Peliharaan untuk apa?");
        System.out.println(KOMPETISI+". Kompetisi");
        System.out.println(PEKERJA_PENJAGA+". Pekerja/Penjaga");
        System.out.println(TERNAK+". Ternak");
        System.out.println(TRANSPORTASI+". Transportasi");
        System.out.println(BEBAS+". Bebas");
        System.out.print("Pilihan: ");
        try {
            String s = br.readLine();
            int result = Integer.parseInt(s);
            if (result < 1 || result > 4){
                result = 0;
            }
            return result;
        } catch (IOException e) {
            return 0;
        }
    }

}

/**
 * Buat ngerun query
 */
class DLQueryEngine {
    private final OWLReasoner reasoner;
    private final DLQueryParser parser;

    public DLQueryEngine(OWLReasoner reasoner, ShortFormProvider shortFormProvider) {
        this.reasoner = reasoner;
        parser = new DLQueryParser(reasoner.getRootOntology(), shortFormProvider);
    }

    public Set<OWLClass> getSuperClasses(String classExpressionString, boolean direct) {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        NodeSet<OWLClass> superClasses = reasoner
                .getSuperClasses(classExpression, direct);
        return superClasses.getFlattened();
    }

    public Set<OWLClass> getEquivalentClasses(String classExpressionString) {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        Node<OWLClass> equivalentClasses = reasoner.getEquivalentClasses(classExpression);
        Set<OWLClass> result = null;
        if (classExpression.isAnonymous()) {
            result = equivalentClasses.getEntities();
        } else {
            result = equivalentClasses.getEntitiesMinus(classExpression.asOWLClass());
        }
        return result;
    }

    public Set<OWLClass> getSubClasses(String classExpressionString, boolean direct) {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        NodeSet<OWLClass> subClasses = reasoner.getSubClasses(classExpression, direct);
        return subClasses.getFlattened();
    }

    public Set<OWLNamedIndividual> getInstances(String classExpressionString,
                                                boolean direct) {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(classExpression,
                direct);
        return individuals.getFlattened();
    }
}

/**
 * Bawaan dari OWL API, gak usah diperhatikan
 */
class DLQueryParser {
    private final OWLOntology rootOntology;
    private final BidirectionalShortFormProvider bidiShortFormProvider;

    public DLQueryParser(OWLOntology rootOntology, ShortFormProvider shortFormProvider) {
        this.rootOntology = rootOntology;
        OWLOntologyManager manager = rootOntology.getOWLOntologyManager();
        Set<OWLOntology> importsClosure = rootOntology.getImportsClosure();
        // Create a bidirectional short form provider to do the actual mapping.
        // It will generate names using the input
        // short form provider.
        bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(manager,
                importsClosure, shortFormProvider);
    }

    public OWLClassExpression parseClassExpression(String classExpressionString) {
        OWLDataFactory dataFactory = rootOntology.getOWLOntologyManager()
                .getOWLDataFactory();
        ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(
                dataFactory, classExpressionString);
        parser.setDefaultOntology(rootOntology);
        OWLEntityChecker entityChecker = new ShortFormEntityChecker(bidiShortFormProvider);
        parser.setOWLEntityChecker(entityChecker);
        return parser.parseClassExpression();
    }
}

class DLQueryPrinter {
    private final DLQueryEngine dlQueryEngine;
    private final ShortFormProvider shortFormProvider;

    public DLQueryPrinter(DLQueryEngine engine, ShortFormProvider shortFormProvider) {
        this.shortFormProvider = shortFormProvider;
        dlQueryEngine = engine;
    }

    /**
     * Buat query
     * @param classExpression input query. contoh "Hewan and Lapangan"
     */
    public void askQuery(String classExpression) {
        if (classExpression.length() == 0) {
            System.out.println("No class expression specified");
        } else {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("\nQUERY:   ").append(classExpression).append("\n\n");
                Set<OWLClass> superClasses = dlQueryEngine.getSuperClasses(
                        classExpression, false);
//                printEntities("SuperClasses", superClasses, sb);
                Set<OWLClass> equivalentClasses = dlQueryEngine
                        .getEquivalentClasses(classExpression);
//                printEntities("EquivalentClasses", equivalentClasses, sb);
                Set<OWLClass> subClasses = dlQueryEngine.getSubClasses(classExpression,
                        true);
//                printEntities("SubClasses", subClasses, sb);
                Set<OWLNamedIndividual> individuals = dlQueryEngine.getInstances(
                        classExpression, false);
                printEntities("Individuals", individuals, sb);
                System.out.println(sb.toString());
            } catch (ParserException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Buat ngeprint hasilnya
     * @param name name
     * @param entities yang mau diprint
     * @param sb hasil printnya
     */
    private void printEntities(String name, Set<? extends OWLEntity> entities,
                               StringBuilder sb) {
        sb.append(name);
        int length = 50 - name.length();
        for (int i = 0; i < length; i++) {
            sb.append(".");
        }
        sb.append("\n\n");
        if (!entities.isEmpty()) {
            for (OWLEntity entity : entities) {
                sb.append("\t").append(shortFormProvider.getShortForm(entity));
                if (entity instanceof OWLNamedIndividualImpl){
                    OWLNamedIndividualImpl owlNamedIndividual = (OWLNamedIndividualImpl) entity;
                    Map<OWLDataPropertyExpression, Set<OWLLiteral>> dataPropertyValues = owlNamedIndividual.getDataPropertyValues(DLQueriesWithHermiT.ONTOLOGY);
                    for(OWLDataPropertyExpression exp : dataPropertyValues.keySet()){
                        Set<OWLLiteral> literals = dataPropertyValues.get(exp);
                        for(OWLLiteral literal: literals){
                            length = 50 - shortFormProvider.getShortForm(entity).length();
                            for (int i = 0; i < length; i++) {
                                sb.append(".");
                            }
                            sb.append("\t").append("Rp").append(literal.parseInteger());
                        }
                    }
                }
                sb.append("\n");
            }
        } else {
            sb.append("\t[NONE]\n");
        }
        sb.append("\n");
    }


}

/**
 * Kelas untuk construct OWL query
 */
class QueryBuilder{
    StringBuilder sb;

    public QueryBuilder(){
        sb = new StringBuilder().append("Hewan");
    }

    public QueryBuilder fungsional(){
        sb.append(" and Fungsional");
        return this;
    }

    public QueryBuilder pekerja(){
        sb.append(" and Pekerja");
        return this;
    }

    public QueryBuilder transportasi(){
        sb.append(" and Transportasi");
        return this;
    }

    public QueryBuilder lapangan(){
        sb.append(" and Lapangan");
        return this;
    }

    public QueryBuilder kompetisi(){
        sb.append(" and Kompetisi");
        return this;
    }

    public QueryBuilder rumah(){
        sb.append(" and Rumah");
        return this;
    }

    public String build(){
        return sb.toString();
    }

    public QueryBuilder kolam() {
        sb.append(" and Kolam");
        return this;
    }

    public QueryBuilder pekerjaPenjaga() {
        sb.append(" and (Pekerja or Penjaga)");
        return this;
    }

    public QueryBuilder ternak() {
        sb.append(" and Ternak");
        return this;
    }

    public QueryBuilder pekarangan() {
        sb.append(" and Pekarangan");
        return this;
    }

    public QueryBuilder darat() {
        sb.append(" and Darat");
        return this;
    }

    public QueryBuilder akuarium() {
        sb.append(" and Akuarium");
        return this;
    }

    public QueryBuilder air() {
        sb.append(" and Air");
        return this;
    }
}