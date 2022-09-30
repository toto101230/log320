package laboratoire2;

public class Noeud {
    private int valeur;
    private int frequence;
    private Noeud gauche;
    private Noeud droit;
    private Noeud parent;

    public Noeud(int valeur,int frequence, Noeud gauche, Noeud droit) {
        this.valeur = valeur;
        this.gauche = gauche;
        this.droit = droit;
        this.frequence = frequence;
    }

    public int getValeur() {
        return valeur;
    }

    public void setValeur(int valeur) {
        this.valeur = valeur;
    }

    public Noeud getGauche() {
        return gauche;
    }

    public void setGauche(Noeud gauche) {
        this.gauche = gauche;
    }

    public Noeud getDroit() {
        return droit;
    }

    public void setDroit(Noeud droit) {
        this.droit = droit;
    }

    public int getFrequence() {
        return frequence;
    }

    public void setFrequence(int frequence) {
        this.frequence = frequence;
    }

    public Noeud getParent() {
        return parent;
    }

    public void setParent(Noeud parent) {
        this.parent = parent;
    }
}
