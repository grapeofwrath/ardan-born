# Wiki generator

Regenerates the [wiki](https://github.com/grapeofwrath/ardan-born/wiki)'s **Ancestries**, **Classes**, **Sizes** and **Powers** pages from the plugin itself. The other pages (Home, Installation, Commands, Arkenstone-Armor, _Sidebar) are edited by hand in the wiki.

Needs only `perl` (with the core `JSON::PP` module) and `git`.

```sh
git clone git@github.com:grapeofwrath/ardan-born.wiki.git /tmp/ardan-born.wiki
perl tools/wiki/generate.pl /tmp/ardan-born.wiki
cd /tmp/ardan-born.wiki && git diff        # review
git commit -am "Update origins and powers" && git push
```

## Where the content comes from

| Content | Source |
|---|---|
| Origins | `src/main/resources/origins/*.json` |
| ArdanBorn's powers | the `super("id", "Title", "Description")` calls in `OriginAbilities.java` |
| Powers from Origins-Reborn and its addons | `external-powers.tsv` (key, title, description; tab-separated) |
| Extra detail on the Powers page (numbers, triggers, how powers combine) | `power-details.md`, one `## <power key>` section per power |

The script stops with an error if an origin uses a power that has no description, and warns if it has no section in `power-details.md`. When you add a power from Origins-Reborn or an addon, add a line to `external-powers.tsv`. For every new power, add a section to `power-details.md`. Its title and description are the strings in that plugin's ability class, e.g.:

```sh
javap -c -p -cp <unzipped jar> com.starshootercity.originsfantasy.abilities.SmallBody | grep 'String '
```
