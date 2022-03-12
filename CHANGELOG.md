![Release status](https://github.com/pci-ua/pci-plugins/actions/workflows/build-release.yml/badge.svg)

# V2
- Serveur HTTP
    - Inclusion d'un système de "rate limit" pour éviter les abus
    - Redirige le navigateur après un envoi réussi
    - Gestion des robots via `/robots.txt`
    - Permet l'affichage d'un message d'erreur customisé
- Gestion des images
    - Meilleure gestion des erreurs d'ImageMagick

# V1
- Zones que les joueurs peuvent claim et influencer
- Affichage d'images envoyées par les utilisateurs sur des cartes en jeu (via un serveur HTTP intégré)