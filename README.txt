1) La 1ere partie à voir sur l'url suivant : http://jsfiddle.net/asidot/cw541n2q/131/

2) La 2 eme partie concerne l'application REST.

2.1- executer le MyServiceTP.java 
2.2- Executer MyClient.java, qui va nous proposer une liste de scénario à éxecuter, il suffit d'entrer le nombre de votre scénario que vous voulez en suivant leur ordre sur le document PDF <Scenario.pdf>.

Quelques explications : 

Modification d'un animal "PUT" :

	Le service utilise le model "Center" pour récupérer les cages et trouver celle qui comporte l'animal à modifier.
	Une fois la cage trouvée l'animal à modifier est effacé de la collection, et la on ajoute l'animal mis à jour grace à la méthode addAnimal() ajoutée dans le model "Center".

Suppression de tout les animaux "DELETE" :

	Le service utilise le model "Center" pour récupérer toutes les cages puis efface toute la collection retournée.
	
Trouver un animal par son nom "find ByName" :

	Une méthode FindAnimalByName() ajouté au model "Center" est utilisée pour trouver le premier animal répondant au nom donné.

Trouver les animaux dans une position "find at " :

	le service récupère la chaine de caractères représentant les coordonnées de la position et la tranforme en un objet de "Position" puis il récupère toutes les cage avant d'utiliser la méthode "equals" de la classe "Position" pour comparer leur position et celle qui est donnée, afin de retourner la cage trouvée.

Trouver les animaux prés d'une position "find at " :

	le service récupère la chaine de caractères représentant les coordonnées de la position et la tranforme en un objet de "Position" puis il récupère toutes les cage avant d'utiliser la méthode "near" ajoutée à la classe "Position" pour comparer (à 1 degrés prés) leur position et celle qui est donnée, afin de retourner la cage la plus proche.
	
