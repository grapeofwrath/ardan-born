# Docs generator

The [docs site](https://grapeofwrath.github.io/ardan-born/) is a [Hugo](https://gohugo.io) site in `docs/`, with its own templates (no theme) and [Pagefind](https://pagefind.app) for search. Its **Ancestries**, **Classes**, **Sizes** and **Powers** pages are generated from the plugin itself. The other pages in `docs/content/` are edited by hand.

The generator needs only `perl` (with the core `JSON::PP` module). Hugo and Pagefind are in the Nix dev shell.

```sh
perl tools/docs/generate.pl   # rewrites docs/content/{ancestries,classes,sizes,powers}.md
hugo server -s docs           # preview at http://localhost:1313/ardan-born/ (search needs a full build, below)
git add docs tools/docs && git commit
```

To try search locally: `hugo -s docs && pagefind --site docs/public`, then serve `docs/public` under `/ardan-born/`.

Pushing to `main` publishes the site (`.github/workflows/docs.yml`). The workflow reruns the generator and fails if the committed pages are out of date, so always commit the regenerated pages along with the code change.

## Writing pages

- Every page starts with front matter giving its title: `---` / `title: "Pearl of Ossë"` / `---`. The title becomes the page's heading, so the body starts at `##`.
- Link to another page by its file, e.g. `[Naugrim](ancestries.md#naugrim-dwarf)`. The build fails on a link to a missing page and warns about a missing `#heading`.
- A new page must be added to `docs/data/nav.toml`, which sets the sidebar and the order the prev/next arrows walk. The build fails if a page is missing from it.
- Each sidebar category has its own page (`origins.md`, `race-items.md`, `lore-and-items.md`), set by `page` in `nav.toml`. Information shared by a category goes there; its pages link to it instead of repeating it.
- Templates are in `docs/layouts/`, styling in `docs/assets/css/ardan.css`.

## Where the content comes from

| Content | Source |
|---|---|
| Origins | `src/main/resources/origins/*.json` |
| ArdanBorn's powers | the `super("id", "Title", "Description")` calls in `OriginAbilities.java` |
| Powers from Origins-Reborn and its addons | `external-powers.tsv` (key, title, description; tab-separated) |
| Extra detail on the Powers page (numbers, triggers, how powers combine) | `power-details.md`, one `## <power key>` section per power |
| Each ancestry's race item | `race-items.tsv` (origin id, page, item name; tab-separated) |

The script stops with an error if an origin uses a power that has no description, or an ancestry has no race item, and warns if a power has no section in `power-details.md`. When you add a power from Origins-Reborn or an addon, add a line to `external-powers.tsv`. For every new power, add a section to `power-details.md`. Its title and description are the strings in that plugin's ability class, e.g.:

```sh
javap -c -p -cp <unzipped jar> com.starshootercity.originsfantasy.abilities.SmallBody | grep 'String '
```
