package com.petoma.expHPbar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class ExpHPbar extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("HPBarPlugin has been enabled!");

        // 定期的に経験値バーを更新するタスクをスケジュール
        new BukkitRunnable() {
            @Override
            public void run() {
                updateXPBars();
            }
        }.runTaskTimer(this, 0L, 10L); // 0.5秒ごとに更新
    }

    @Override
    public void onDisable() {
        getLogger().info("HPBarPlugin has been disabled!");
    }

    /**
     * プレイヤーの経験値バーを更新する
     */
    private void updateXPBars() {
        // 最大HP（t1_HPの最大値）を取得
        Integer maxHP = getMaxHP();
        if (maxHP == null || maxHP <= 0) {
            return; // 最大HPが無効な場合は更新しない
        }

        // 全プレイヤーをチェック
        for (Player player : Bukkit.getOnlinePlayers()) {
            // プレイヤーの現在のHPを取得
            Integer currentHP = getScore(player, "t1_HP");

            // スコアボードに登録されていないプレイヤーはスキップ
            if (currentHP == null || currentHP <= 0) {
                player.setExp(0); // 経験値バーを空に設定
                continue;
            }

            // 経験値バーを計算して設定 (割合を小数で計算)
            float ratio = Math.min((float) currentHP / maxHP, 1.0f); // 最大1.0
            player.setLevel(0); // レベル0で固定
            player.setExp(ratio); // 経験値バーを割合で設定

            // デバッグ用のログ出力
            //getLogger().info(player.getName() + " - currentHP: " + currentHP + ", maxHP: " + maxHP + ", ratio: " + ratio);
        }
    }

    /**
     * スコアボードから最大HPの値を取得する
     *
     * @return 最大HP (存在しない場合は null)
     */
    private Integer getMaxHP() {
        // 最大HPのスコアを取得（t1_HPの最大値が設定されていると仮定）
        Integer maxHP = getScore(null, "t1_HP"); // ここで取得するスコアは全プレイヤー共通の最大値
        return maxHP;
    }

    /**
     * スコアボードからスコアを取得する
     *
     * @param player プレイヤー
     * @param objectiveName オブジェクティブ名
     * @return スコア (存在しない場合は null)
     */
    private Integer getScore(Player player, String objectiveName) {
        if (Bukkit.getScoreboardManager() == null) return null;

        var scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        var objective = scoreboard.getObjective(objectiveName);
        if (objective == null) return null;

        if (player == null) {
            // 最大HPはプレイヤー名ではなく、共通のスコアを取得
            var score = objective.getScore("max"); // 「max」として登録された最大HPのスコアを取得
            return score.isScoreSet() ? score.getScore() : null;
        } else {
            var score = objective.getScore(player.getName());
            return score.isScoreSet() ? score.getScore() : null;
        }
    }
}
