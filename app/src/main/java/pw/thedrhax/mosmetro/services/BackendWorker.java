/**
 * Wi-Fi в метро (pw.thedrhax.mosmetro, Moscow Wi-Fi autologin)
 * Copyright © 2015 Dmitry Karikh <the.dr.hax@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pw.thedrhax.mosmetro.services;

import java.util.concurrent.TimeUnit;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import pw.thedrhax.mosmetro.updater.BackendRequest;

public class BackendWorker extends Worker {
    private final Context context;

    public BackendWorker(Context context, WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @Override @NonNull
    public Result doWork() {
        BackendRequest task = new BackendRequest(context);
        if (task.run()) {
            return Result.success();
        } else {
            return Result.retry();
        }
    }

    public static void configure(Context context) {
        Constraints constraints = new Constraints(NetworkType.UNMETERED, false, false, false);

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(BackendWorker.class, 3, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setInitialDelay(10, TimeUnit.MINUTES)
                .addTag("BackendWorker")
                .build();

        WorkManager manager = WorkManager.getInstance(context);

        manager.enqueueUniquePeriodicWork(
            "BackendWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        );
    }
}
