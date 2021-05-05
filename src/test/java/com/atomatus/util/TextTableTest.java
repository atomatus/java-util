package com.atomatus.util;

import junit.framework.TestCase;

public class TextTableTest extends TestCase {

    private TextTable tt0, tt1, tt2, tt3;

    @Override
    protected void setUp() {
        tt0 = new TextTable
                .Builder()
                .from(new Object[][] {
                        { "id", "First Name", "Last Name", "Age", "Profile" },

                        { 1, "Sheldon", "Cooper", 40, "Possui um temperamento arrogante, chegando a pensar que so apareceria alguem mais inteligente que ele apos centenas de anos." },

                        { 2, "Leonard", "Hofstadter", 40, "Um fisico experimental que recebeu seu doutorado quando ele tinha 24 anos de idade." },

                        { 3, "Howard", "Wolowitz", 42, "Um engenheiro espacial do Instituto de Tecnologia da California do departamento de Fisica Aplicada com mestrado em Engenharia." },

                        { 4, "Rajesh", "Koothrappali", "", "Originalmente de Nova Delhi, trabalha no departamento de Fisica na Caltech como astrofisico." } })
                .lineSeparator()
                .maxWidth(35)
                //.noWrap()
                //.label(new String[] { "", "1", "2", "3", "4", "5" })
                .build();

        tt1 = new TextTable
                .Builder()
                .columns(new String[] { "id", "First Name", "Last Name", "Age", "Profile" })
                .rows(new Object[][] {
                        { 1, "Sheldon", "Cooper", 40, "Possui um temperamento arrogante, chegando a pensar que so apareceria alguem mais inteligente que ele apos centenas de anos." },
                        { 2, "Leonard", "Hofstadter", 40, "Um fisico experimental que recebeu seu doutorado quando ele tinha 24 anos de idade." },
                        { 3, "Howard", "Wolowitz", 42, "Um engenheiro espacial do Instituto de Tecnologia da California do departamento de Fisica Aplicada com mestrado em Engenharia." },
                        { 4, "Rajesh", "Koothrappali", "", "Originalmente de Nova Delhi, trabalha no departamento de Fisica na Caltech como astrofisico." }
                })
                .lineSeparator()
                .maxWidth(35)
                .build();

        tt2 = new TextTable
                .Builder()
                .column("id")
                .column("First Name")
                .column("Last Name")
                .column("Age")
                .column("Profile")
                .row(new Object[] { 1, "Sheldon", "Cooper", 40, "Possui um temperamento arrogante, chegando a pensar que so apareceria alguem mais inteligente que ele apos centenas de anos." })
                .row(new Object[] { 2, "Leonard", "Hofstadter", 40, "Um fisico experimental que recebeu seu doutorado quando ele tinha 24 anos de idade." })
                .row(new Object[] { 3, "Howard", "Wolowitz", 42, "Um engenheiro espacial do Instituto de Tecnologia da California do departamento de Fisica Aplicada com mestrado em Engenharia." })
                .row(new Object[] { 4, "Rajesh", "Koothrappali", "", "Originalmente de Nova Delhi, trabalha no departamento de Fisica na Caltech como astrofisico." })
                .lineSeparator()
                .maxWidth(35)
                .build();

        tt3 = new TextTable
                .Builder()
                .column("id")
                .column("First Name")
                .column("Last Name")
                .column("Age")
                .column("Profile")
                .row().cell(1).cell("Sheldon").cell("Cooper").cell(40).cell("Possui um temperamento arrogante, chegando a pensar que so apareceria alguem mais inteligente que ele apos centenas de anos.")
                .row().cell(2).cell("Leonard").cell("Hofstadter").cell(40).cell("Um fisico experimental que recebeu seu doutorado quando ele tinha 24 anos de idade.")
                .row().cell(3).cell("Howard").cell("Wolowitz").cell(42).cell("Um engenheiro espacial do Instituto de Tecnologia da California do departamento de Fisica Aplicada com mestrado em Engenharia.")
                .row().cell(4).cell("Rajesh").cell("Koothrappali").cell("").cell("Originalmente de Nova Delhi, trabalha no departamento de Fisica na Caltech como astrofisico.")
                .lineSeparator()
                .maxWidth(35)
                .build();

    }

    @Override
    protected void tearDown() {
        tt0.close();
        tt1.close();
        tt2.close();
        tt3.close();
        tt0 = null;
        tt1 = null;
        tt2 = null;
        tt3 = null;
    }

    public void testBuild() {
        assertEquals(tt0.table.length, tt1.table.length);
        assertEquals(tt0.table.length, tt2.table.length);
        assertEquals(tt0.table.length, tt3.table.length);

        for(int i=0, r=tt0.table.length; i < r; i++) {
            assertEquals(tt0.table[i].length, tt1.table[i].length);
            assertEquals(tt0.table[i].length, tt2.table[i].length);
            assertEquals(tt0.table[i].length, tt3.table[i].length);
            for(int j=0, c=tt0.table[i].length; j < c; j++) {
                assertEquals(tt0.table[i][j], tt1.table[i][j]);
                assertEquals(tt0.table[i][j], tt2.table[i][j]);
                assertEquals(tt0.table[i][j], tt3.table[i][j]);
            }
        }
    }

    public void testPrint() {
        tt0.print();
    }

}