#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "myString.h"
#include "global.h"
#include "liste.h"
#include "typeElement.h"
#include "donnee.h"
#include "tuple.h"
#include "colonne.h"
#include "table.h"


void addTable(List *listeTables, const char *nomTable)
{
    DataValue dvTable;

    if(!listeTables || (listeTables->type != TABLE) || !nomTable)return;

    dvTable = createTable(nomTable);
    addAsLast(listeTables,dvTable);
}

int containsTable(const List *listeTables, char *nomTable)
{
    int res;
    DataValue dvTmpTable;

    if(!listeTables || (listeTables->type != TABLE) || !nomTable)return 0;

    dvTmpTable = createTable(nomTable);
    res = isInList(listeTables,compareTable,dvTmpTable);
    destroyTable(dvTmpTable);
    return res;
}

int containsAllTables(const List *listeTables, char **nomsTables, int nbTables)
{
	int i;
	if(!listeTables || (listeTables->type != TABLE) || !nomsTables)return 0;

	for(i=0;i<nbTables;i++)
	{
		if(!containsTable(listeTables,nomsTables[i]))
		{
			return 0;
		}
	}
	return 1;
}

int compareTable(const DataValue dv1, const DataValue dv2)
{
    return mystrcasecmp(dv1.table->nom,dv2.table->nom);
}

void displayTable(const DataValue dv,FILE *outputFile)
{
    printf("%s",dv.table->nom);
    if(outputFile)
    	fprintf(outputFile,"%s",dv.table->nom);
}

void displayAllTables(const List *listeTables,FILE *outputFile)
{
    if(!listeTables || listeTables->type != TABLE) return;
    displayList(listeTables,displayTable,SEPTABLES,outputFile);
}

int removeTable(List *listeTable, char *nomTable)
{
    ListNode *tmpLN;
    DataValue dvTmpTable;

    if(!listeTable || listeTable->type != TABLE || !nomTable) return 0;

    dvTmpTable = createTable(nomTable);
    tmpLN = removeElement(listeTable,compareTable,dvTmpTable);
    destroyTable(dvTmpTable);
    if(tmpLN != NULL)
    {
        destroyTable(tmpLN->valeur);
        free(tmpLN);
        return 1;
    }
    return 0;
}

Table *getTable(const List *listeTable, char *nomTable)
{
    ListNode *elementTable;
    DataValue dvTmpTable;

    if(!listeTable || listeTable->type != TABLE || !nomTable) return NULL;

    dvTmpTable = createTable(nomTable);
    elementTable = getElement(listeTable,compareTable,dvTmpTable);
    destroyTable(dvTmpTable);
    if(elementTable == NULL) return NULL;
    return elementTable->valeur.table;
}

void destroyTable(DataValue dv)
{
    destroyList(dv.table->listeColonne,destroyCol);
    destroyList(dv.table->listeTuple,destroyTuple);
    free(dv.table->nom);
    free(dv.table);
}

DataValue createTable(const char *nomTable)
{
    DataValue dvTable;
    Table *newTable;

    if(!nomTable) return dvTable;

    newTable = malloc(sizeof(Table));
    testerPointeur(newTable);
    newTable->nom = mystrdup(nomTable);
    newTable->listeColonne = newList(COLONNE);
    newTable->listeTuple = newList(TUPLE);
    dvTable.table = newTable;
    return dvTable;
}
